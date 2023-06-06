package vova.group.id.LibraryBoot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vova.group.id.LibraryBoot.models.Person;


@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Person findByEmail(String email);
}
