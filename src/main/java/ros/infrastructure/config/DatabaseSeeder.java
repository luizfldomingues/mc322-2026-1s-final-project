package ros.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ros.infrastructure.persistence.entity.AdminUserEntity;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Long menuCount = entityManager.createQuery("SELECT COUNT(m) FROM MenuItemEntity m", Long.class).getSingleResult();

        if (menuCount == 0) {
            System.out.println("Criando banco de dados com itens iniciais...");

            MenuItemEntity item1 = new MenuItemEntity();
            item1.setName("Hambúrguer Clássico");
            item1.setDescription("Pão de brioche, carne de 150g, queijo cheddar e molho especial.");
            item1.setPrice(28.90);
            item1.setCategory("Hambúrgueres");
            item1.setAvailable(true);
            entityManager.persist(item1);

            MenuItemEntity item2 = new MenuItemEntity();
            item2.setName("Batata Frita Rústica");
            item2.setDescription("Batatas com corte rústico, temperadas com páprica e alecrim.");
            item2.setPrice(15.00);
            item2.setCategory("Acompanhamentos");
            item2.setAvailable(true);
            entityManager.persist(item2);

            MenuItemEntity item3 = new MenuItemEntity();
            item3.setName("Refrigerante de Lata");
            item3.setDescription("Lata de 350ml.");
            item3.setPrice(6.00);
            item3.setCategory("Bebidas");
            item3.setAvailable(true);
            entityManager.persist(item3);

            AdminUserEntity admin = new AdminUserEntity();
            admin.setUsername("admin");
            admin.setPasswordHash("admin");
            entityManager.persist(admin);

            System.out.println("Banco de dados criado com sucesso!");
        }
    }
}
