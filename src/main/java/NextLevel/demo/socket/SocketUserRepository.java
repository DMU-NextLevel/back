package NextLevel.demo.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("socketUserRepository")
@RequiredArgsConstructor
@Slf4j
public class SocketUserRepository implements SocketRepository {

    private Map<Long, SocketUserInfo> repo = new HashMap<>();

    @Override
    public Optional<SocketUserInfo> findSocket(long userId) {
        printCount();
        return Optional.ofNullable(repo.get(userId));
    }

    @Override
    public void saveSocket(SocketUserInfo socket) {
        printCount();
        repo.put(socket.getUserId(), socket); // 예전 값이 있든 없든 덮어쓰기 됨
    }

    @Override
    public void deleteSocket(long userId) {
        repo.remove(userId);
    }

    @Override
    public Collection<SocketUserInfo> findAll() {
        printCount();
        return repo.values();
    }

    private void printCount() {
        log.info("user socket repository size :: " + repo.size());
    }
}
