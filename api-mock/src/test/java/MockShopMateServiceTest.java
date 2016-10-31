import com.google.common.collect.ImmutableMap;
import com.shopmate.api.MockShopMateService;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class MockShopMateServiceTest {

    private static final String TestListTitle = "Test List";

    private ShopMateService service;

    @Before
    public void before() {
        service = new MockShopMateService();
    }

    @Test
    public void testCreateShoppingListAsync() throws ExecutionException, InterruptedException {
        ShoppingList list = service.createShoppingListAsync("foo", TestListTitle).get().getList();

        Assert.assertEquals(MockShopMateService.USER_ID, list.getCreatorId());
        Assert.assertEquals(TestListTitle, list.getTitle());
        Assert.assertTrue(list.getMemberIds().contains(MockShopMateService.USER_ID));
        Assert.assertEquals(0, list.getItemIds().size());
    }

    @Test
    public void testCreateAndGetShoppingList() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result = service.createShoppingListAsync("foo", TestListTitle).get();
        long id = result.getId();

        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync("foo").get();
        Assert.assertEquals(1, lists.size());
        Assert.assertTrue(lists.containsKey(id));

        ShoppingList list = lists.get(id);
        Assert.assertEquals(MockShopMateService.USER_ID, list.getCreatorId());
        Assert.assertEquals(TestListTitle, list.getTitle());
        Assert.assertTrue(list.getMemberIds().contains(MockShopMateService.USER_ID));
        Assert.assertEquals(0, list.getItemIds().size());
    }

    @Test
    public void testCreateAndGetMultipleShoppingLists() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result1 = service.createShoppingListAsync("foo", TestListTitle).get();
        CreateShoppingListResult result2 = service.createShoppingListAsync("foo", TestListTitle).get();

        Assert.assertNotEquals(result1.getId(), result2.getId());

        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync("foo").get();
        Assert.assertEquals(2, lists.size());
        Assert.assertTrue(lists.containsKey(result1.getId()));
        Assert.assertTrue(lists.containsKey(result2.getId()));
    }
}
