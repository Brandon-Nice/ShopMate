import com.shopmate.api.ShopMateService;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemBuilder;
import com.shopmate.api.model.item.ShoppingListItemHandle;
import com.shopmate.api.model.item.ShoppingListItemPriority;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllShoppingListsResult;
import com.shopmate.api.net.NetShopMateService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NetShopMateServiceTest {

    // Set this to a token from a test user for testing to work...
    // TODO: Create a dummy token on the server or something which always validates?
    private static final String TestToken = "EAAQZAwjV0NNsBACC1T7QzZAwzlZA4TCB0wX20jpG4vZBgaTiSpLSkoYD53OEXrqyHmsLPq5miW1FgZC7YgFZB9zRtZChpvV8s2bTSyWFGZC6yKpEwZCDVGjGJeODTeL82BxTFwEueQZAsBRYZAzZAMsdKZCwVNHyGIplO53MsccuL3JSJDrikd03ZAqnKR";
    private static final String TestId = "136682413460238";

    private static final String TestListName = "Test List";

    private static final String TestItemName = "Test Item";
    private static final String TestItemDescription = "Foo";
    private static final String TestItemImage = "https://i.imgur.com/bWltonq.png";
    private static final int TestItemPrice = 1000;
    private static final int TestItemQuantity = 2;
    private static final int TestItemQuantityPurchased = 1;
    private static final ShoppingListItemPriority TestItemPriority = ShoppingListItemPriority.NORMAL;

    private ShopMateService service;

    @Before
    public void before() {
        service = new NetShopMateService();
    }

    @Test
    public void testCreatingList() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result = service.createListAsync(TestToken, TestListName).get();
        Assert.assertEquals(TestListName, result.getList().getTitle());
        Assert.assertEquals(TestId, result.getList().getCreatorId());
        Assert.assertTrue(result.getList().getMemberIds().contains(TestId));
        Assert.assertEquals(0, result.getList().getItems().size());
    }

    @Test
    public void testCreatingAndGettingList() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result = service.createListAsync(TestToken, TestListName).get();
        ShoppingList expected = result.getList();
        ShoppingList actual = service.getListAndItemsAsync(TestToken, result.getId()).get();
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getCreatorId(), actual.getCreatorId());
    }

    @Test
    public void testGettingAllListsNoItems() throws ExecutionException, InterruptedException {
        GetAllShoppingListsResult lists = service.getAllListsNoItemsAsync(TestToken).get();
        for (ShoppingList list : lists.getLists().values()) {
            Assert.assertTrue(list.getMemberIds().contains(TestId));
            for (ShoppingListItemHandle item : list.getItems()) {
                Assert.assertTrue(!item.getItem().isPresent());
            }
        }
    }

    @Test
    public void testGettingAllListsAndItems() throws ExecutionException, InterruptedException {
        GetAllShoppingListsResult lists = service.getAllListsAndItemsAsync(TestToken).get();
        for (ShoppingList list : lists.getLists().values()) {
            Assert.assertTrue(list.getMemberIds().contains(TestId));
            for (ShoppingListItemHandle item : list.getItems()) {
                Assert.assertTrue(item.getItem().isPresent());
            }
        }
    }

    @Test
    public void testCreatingItem() throws ExecutionException, InterruptedException {
        CreateShoppingListResult createListResult = service.createListAsync(TestToken, TestListName).get();
        ShoppingListItem testItem = new ShoppingListItemBuilder(TestItemName)
                .description(TestItemDescription)
                .imageUrl(TestItemImage)
                .maxPriceCents(TestItemPrice)
                .quantity(TestItemQuantity)
                .quantityPurchased(TestItemQuantityPurchased)
                .priority(TestItemPriority)
                .build();
        CreateShoppingListItemResult createItemResult = service.createItemAsync(TestToken, createListResult.getId(), testItem).get();
        ShoppingListItem createdItem = createItemResult.getItem();
        Assert.assertEquals(testItem.getName(), createdItem.getName());
        Assert.assertEquals(testItem.getDescription(), createdItem.getDescription());
        Assert.assertTrue(testItem.getImageUrl().isPresent());
        Assert.assertEquals(testItem.getImageUrl().get(), createdItem.getImageUrl().get());
        Assert.assertTrue(testItem.getMaxPriceCents().isPresent());
        Assert.assertEquals(testItem.getQuantity(), createdItem.getQuantity());
        Assert.assertEquals(testItem.getQuantityPurchased(), createdItem.getQuantityPurchased());
        Assert.assertEquals(testItem.getPriority(), createdItem.getPriority());
    }

    @Test
    public void testCreatingAndGettingItem() throws ExecutionException, InterruptedException {
        CreateShoppingListResult createListResult = service.createListAsync(TestToken, TestListName).get();

        ShoppingListItem testItem = new ShoppingListItemBuilder(TestItemName)
                .description(TestItemDescription)
                .imageUrl(TestItemImage)
                .maxPriceCents(TestItemPrice)
                .quantity(TestItemQuantity)
                .quantityPurchased(TestItemQuantityPurchased)
                .priority(TestItemPriority)
                .build();
        CreateShoppingListItemResult createItemResult = service.createItemAsync(TestToken, createListResult.getId(), testItem).get();

        ShoppingListItem gotItem = service.getItemAsync(TestToken, createItemResult.getId()).get();
        Assert.assertEquals(testItem.getName(), gotItem.getName());
        Assert.assertEquals(testItem.getDescription(), gotItem.getDescription());
        Assert.assertTrue(testItem.getImageUrl().isPresent());
        Assert.assertEquals(testItem.getImageUrl().get(), gotItem.getImageUrl().get());
        Assert.assertTrue(testItem.getMaxPriceCents().isPresent());
        Assert.assertEquals(testItem.getQuantity(), gotItem.getQuantity());
        Assert.assertEquals(testItem.getQuantityPurchased(), gotItem.getQuantityPurchased());
        Assert.assertEquals(testItem.getPriority(), gotItem.getPriority());
    }
}
