package NextLevel.demo.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SocketServiceImpl implements SocketService {

    public SocketServiceImpl(
            @Qualifier("socketUserRepository") SocketRepository socketUserRepository,
            @Qualifier("socketAnonymousRepository") SocketRepository socketAnonymousRepository)
    {
        this.socketUserRepository = socketUserRepository;
        this.socketAnonymousRepository = socketAnonymousRepository;
    }

    private final SocketRepository socketUserRepository;
    private final SocketRepository socketAnonymousRepository;

    private static final String CONNECT_DATA = "connect";

    @Value("${sse.connection_millisecond}")
    private Long SSE_CONNECTION_TIME;

    @Value("${sse.re_connection_second}")
    private Long SEE_RECONNECTION_SECOND;

    @Override
    public SseEmitter getSocket(Long userId) {
        if(userId != null)
            return socketUserRepository.findSocket(userId).orElseGet(
                    ()->createSocket(userId, socketUserRepository)
            ).getSocket();
        else
            return createSocket(null, socketAnonymousRepository).getSocket();
    }

    private SocketUserInfo createSocket(Long userId, SocketRepository socketRepository) {
        SseEmitter emitter = new SseEmitter(SSE_CONNECTION_TIME);
        emitter.onTimeout(()->socketRepository.deleteSocket(userId));
        emitter.onCompletion(()->socketRepository.deleteSocket(userId));
        emitter.onError((e)->socketRepository.deleteSocket(userId));

        SocketUserInfo newSocket = new SocketUserInfo(userId, emitter);
        if(userId != null)
            socketUserRepository.saveSocket(newSocket);
        else
            socketAnonymousRepository.saveSocket(newSocket);

        sendSocket(emitter, SocketType.CONNECT, new SocketDataDto(CONNECT_DATA, LocalDateTime.now()));

        log.info("emitter created");

        return newSocket;
    }

    @Override
    public void sendSocket(long userId, SocketType type, SocketDataDto data) {
        Optional<SocketUserInfo> socketInfoOpt = socketUserRepository.findSocket(userId);
        if(socketInfoOpt.isEmpty())
            return;

        SocketUserInfo socketInfo = socketInfoOpt.get();

        if(!isConnected(socketInfo)){
            log.info("socket info is null " + socketInfo.toString());
            deleteSocket(socketInfo);
            return;
        }

        sendSocket(socketInfo.getSocket(), type, data);
    }

    @Override
    public void sendSocketAll(SocketType type, SocketDataDto data) {
        socketUserRepository.findAll().forEach((socketUserInfo)->sendSocket(socketUserInfo.getSocket(), type, data));

        Collection<SocketUserInfo> anonymousSockets = socketAnonymousRepository.findAll();
        Iterator<SocketUserInfo> iter = anonymousSockets.iterator();
        while(iter.hasNext()){
            try{sendSocket(iter.next().getSocket(), type, data);}
            catch (SocketSendFailException e) {
                iter.remove();
            }
        }

    }

    private void sendSocket(SseEmitter emitter, SocketType type, SocketDataDto data){
        if(data == null || data.getText() == null){
            log.info("data is null " + data.toString());
            return;
        }
        try {
//            socketInfo.getSocket().send(SseEmitter.event().name(type.name()).data(data.toString()).reconnectTime(
//                    SEE_RECONNECTION_SECOND * 1000).build());
            emitter.send(SseEmitter.event().name(type.name()).data(data.toString()).build());
            log.info("sse emitter send data " + data.toString());
        } catch (Exception e) {
            log.info("sse send fail " + data.toString());
            emitter.complete();
            throw new SocketSendFailException();
        }
    }

    private void deleteSocket(SocketUserInfo socketInfo) {
        if(isConnected(socketInfo)){
            socketInfo.getSocket().complete();
        }
        if(socketInfo.getUserId() != null)
            socketUserRepository.deleteSocket(socketInfo.getUserId());
    }

    private boolean isConnected(SocketUserInfo socketInfo) {
        return socketInfo != null && socketInfo.getSocket() != null;
    }
}
