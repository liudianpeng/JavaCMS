package testBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import testBlog.entity.Tag;

public interface TagRepository extends JpaRepository<Tag,Integer> {
    Tag findByName(String name);
}
