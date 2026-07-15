package ros.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import ros.infrastructure.persistence.entity.OrderEntity;
import ros.infrastructure.persistence.entity.OrderItemEntity;
import ros.domain.model.OrderStatus;
import java.time.LocalDateTime;
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

            // Seeding Example Orders
            System.out.println("Semeando pedidos de exemplo...");

            // Order 1: Table 4, PENDING, 2x Burger, 1x Soda
            OrderEntity order1 = new OrderEntity();
            order1.setCustomerName("Maria Souza");
            order1.setTableNumber("4");
            order1.setStatus(OrderStatus.PENDING);
            order1.setCreatedAt(LocalDateTime.now());
            entityManager.persist(order1);

            OrderItemEntity order1Item1 = new OrderItemEntity();
            order1Item1.setMenuItem(item1);
            order1Item1.setQuantity(2);
            order1Item1.setPriceAtPurchase(item1.getPrice());
            order1Item1.setOrder(order1);
            entityManager.persist(order1Item1);
            order1.addOrderItem(order1Item1);

            OrderItemEntity order1Item2 = new OrderItemEntity();
            order1Item2.setMenuItem(item3);
            order1Item2.setQuantity(1);
            order1Item2.setPriceAtPurchase(item3.getPrice());
            order1Item2.setOrder(order1);
            entityManager.persist(order1Item2);
            order1.addOrderItem(order1Item2);

            // Order 2: Table 8, READY, 1x Fries
            OrderEntity order2 = new OrderEntity();
            order2.setCustomerName("João Silva");
            order2.setTableNumber("8");
            order2.setStatus(OrderStatus.READY);
            order2.setCreatedAt(LocalDateTime.now().minusMinutes(30));
            entityManager.persist(order2);

            OrderItemEntity order2Item1 = new OrderItemEntity();
            order2Item1.setMenuItem(item2);
            order2Item1.setQuantity(1);
            order2Item1.setPriceAtPurchase(item2.getPrice());
            order2Item1.setOrder(order2);
            entityManager.persist(order2Item1);
            order2.addOrderItem(order2Item1);

            System.out.println("Banco de dados criado com sucesso!");
        }
    }
}
