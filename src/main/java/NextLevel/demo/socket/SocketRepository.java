package NextLevel.demo.socket;

import java.util.Collection;
import java.util.Optional;

public interface SocketRepository {

    Optional<SocketUserInfo> findSocket(long userId);

    void saveSocket(SocketUserInfo socket);

    void deleteSocket(long userId);

    Collection<SocketUserInfo> findAll();

}
