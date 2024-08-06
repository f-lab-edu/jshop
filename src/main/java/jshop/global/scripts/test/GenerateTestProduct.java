package jshop.global.scripts.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jshop.core.domain.product.dto.CreateProductDetailRequest;
import jshop.core.domain.product.dto.CreateProductRequest;
import jshop.core.domain.product.entity.Product;
import jshop.core.domain.product.service.ProductService;
import jshop.core.domain.user.dto.UserType;
import jshop.core.domain.user.entity.User;
import jshop.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("generate-test-data")
@RequiredArgsConstructor
public class GenerateTestProduct implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>(
            userRepository.findUsersByUserType(UserType.SELLER));
        ExecutorService executors = Executors.newFixedThreadPool(10);

        while (users.peek().getId() < 592000) {
            users.poll();
        }

        int N = users.size();
        log.warn("users : {}", users.size());

        for (int userI = 0; userI < N; userI++) {
            executors.submit(() -> {
                int productCount = 0;
                int productDetailCount = 0;

                User user = users.poll();
                log.warn("user : {} start", user.getId());
                int productN = random.nextInt(1, 1000);
                long categoryId = (long) random.nextInt(1, 20);
                List<Long> productIds = new ArrayList<>();

                for (int i = 0; i < productN; i++) {
                    CreateProductRequest createProductRequest = CreateProductRequest
                        .builder()
                        .categoryId(categoryId)
                        .name(
                            faker.color().name() + " " + faker.commerce().material() + " " + faker
                                .commerce()
                                .productName() + " " + user.getUsername())
                        .manufacturer(faker.commerce().brand())
                        .description(faker.lorem().sentence())
                        .attributes(createAttributes())
                        .build();

                    try {
                        Long productId = productService.createProduct(createProductRequest, user.getId());
                        productCount++;
                        productIds.add(productId);
                    } catch (Exception e) {
                        log.error("product", e);
                    }
                }

                for (Long productId : productIds) {
                    long price = random.nextLong(100) * 100L;
                    Product product = productService.getProduct(productId);
                    List<Map<String, String>> attributeList = getAttributeList(product);

                    for (Map<String, String> attribute : attributeList) {
                        CreateProductDetailRequest createProductDetailRequest = CreateProductDetailRequest
                            .builder()
                            .price(price)
                            .attribute(attribute)
                            .build();

                        try {
                            productService.createProductDetail(createProductDetailRequest, productId);
                            productDetailCount++;
                        } catch (Exception e) {
                            log.error("pd", e);
                            log.error("product : {}", product.getAttributes());
                            log.error("productDetails : {}", attributeList);
                        }

                    }
                }

                log.warn("user : {} product : {}, product det ail : {}", user.getId(), productCount,
                    productDetailCount);
            });
        }
    }

    private List<Map<String, String>> getAttributeList(Product product) {
        Map<String, List<String>> attributes = product.getAttributes();

        Set<String> keys = attributes.keySet();

        List<Map<String, String>> attributeList = new ArrayList<>();

        for (String key : keys) {
            if (attributeList.isEmpty()) {
                for (String value : attributes.get(key)) {
                    Map<String, String> t = new HashMap<>();
                    t.put(key, value);
                    attributeList.add(t);
                }
            } else {
                List<Map<String, String>> new_attributeList = new ArrayList<>();
                for (Map<String, String> attribute : attributeList) {
                    for (String value : attributes.get(key)) {
                        Map<String, String> tmp = new HashMap<>(attribute);
                        tmp.put(key, value);
                        new_attributeList.add(tmp);
                    }
                }
                attributeList = new_attributeList;
            }
        }
        return attributeList;
    }

    private Map<String, List<String>> createAttributes() {
        Map<String, List<String>> attributes = new HashMap<>();
        Random random = new Random();
        Faker faker = new Faker();

        for (int i = 0; i < 3; i++) {
            int type = random.nextInt(14);
            Set<String> values = new HashSet<>();
            int n = random.nextInt(5);

            switch (type) {
                case 0:
                    List<String> colors = new ArrayList<>();

                    for (int j = 0; j < n; j++) {
                        String name = faker.color().name();
                        if (!values.contains(name)) {
                            colors.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("color", colors);
                    }
                    break;

                case 1:
                    List<String> materials = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.commerce().material();
                        if (!values.contains(name)) {
                            materials.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("material", materials);
                    }
                    break;

                case 2:
                    List<String> sizes = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.size().adjective();
                        if (!values.contains(name)) {
                            sizes.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("size", sizes);
                    }
                    break;
                case 3:
                    List<String> genres = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.book().genre();
                        if (!values.contains(name)) {
                            genres.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("genre", genres);
                    }
                    break;
                case 4:
                    List<String> venders = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.commerce().vendor();
                        if (!values.contains(name)) {
                            venders.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("vendor", venders);
                    }
                    break;

                case 5:
                    List<String> animals = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.animal().genus();
                        if (!values.contains(name)) {
                            animals.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("animal", animals);
                    }
                    break;

                case 6:
                    List<String> cats = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.cat().breed();
                        if (!values.contains(name)) {
                            cats.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("cat", cats);
                    }
                    break;

                case 7:
                    List<String> capitals = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.country().capital();
                        if (!values.contains(name)) {
                            capitals.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("capital", capitals);
                    }
                    break;

                case 8:
                    List<String> eldenrings = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.eldenRing().weapon();
                        if (!values.contains(name)) {
                            eldenrings.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("eldenring", eldenrings);
                    }
                    break;
                case 9:
                    List<String> coffees = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.coffee().blendName();
                        if (!values.contains(name)) {
                            coffees.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("coffee", coffees);
                    }
                    break;
                case 10:
                    List<String> kpops = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.kpop().girlGroups();
                        if (!values.contains(name)) {
                            kpops.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("kpop", kpops);
                    }
                    break;

                case 11:
                    List<String> money = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.money().currency();
                        if (!values.contains(name)) {
                            money.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("currency", money);
                    }
                    break;

                case 12:
                    List<String> space = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.space().galaxy();
                        if (!values.contains(name)) {
                            space.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("space", space);
                    }
                    break;

                case 13:
                    List<String> temp = new ArrayList<>();
                    for (int j = 0; j < n; j++) {
                        String name = faker.weather().temperatureCelsius();
                        if (!values.contains(name)) {
                            temp.add(name);
                            values.add(name);
                        }
                    }
                    if (n > 0) {
                        attributes.put("temperature", temp);
                    }
                    break;
            }
        }
        return attributes;
    }
}
