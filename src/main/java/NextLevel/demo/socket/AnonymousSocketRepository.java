package NextLevel.demo.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("socketAnonymousRepository")
@Slf4j
public class AnonymousSocketRepository implements SocketRepository {

    List<SocketUserInfo> repo = new ArrayList<>();

    @Override
    public Optional<SocketUserInfo> findSocket(long userId) {
        return Optional.empty();
    }

    @Override
    public void saveSocket(SocketUserInfo socket) {
        repo.add(socket);
    }

    @Override
    public void deleteSocket(long userId) {

    }

    @Override
    public Collection<SocketUserInfo> findAll() {
        return repo;
    }

}
