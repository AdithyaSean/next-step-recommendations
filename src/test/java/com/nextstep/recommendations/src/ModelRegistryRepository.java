//package com.nextstep.recommendations.src;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.Optional;
//
//public interface ModelRegistryRepository extends JpaRepository<ModelRegistry, Long> {
//    Optional<ModelRegistry> findFirstByStreamIdOrderByVersionDesc(Integer streamId);
//    Optional<ModelRegistry> findFirstByStreamIdIsNullOrderByVersionDesc();
//}
