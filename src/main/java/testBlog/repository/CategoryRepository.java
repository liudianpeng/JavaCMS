package testBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import testBlog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer> {

}
