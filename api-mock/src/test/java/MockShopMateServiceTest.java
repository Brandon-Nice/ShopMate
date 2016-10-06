import com.google.common.collect.ImmutableMap;
import com.shopmate.api.MockShopMateService;
import com.shopmate.api.ShopMateErrorCode;
import com.shopmate.api.ShopMateException;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.LogInResult;

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
    public void testLogInAsync() throws ExecutionException, InterruptedException {
        LogInResult result1 = service.logInAsync("foo").get();
        LogInResult result2 = service.logInAsync("bar").get();
        Assert.assertEquals("foo", result1.getSession().getSessionToken());
        Assert.assertEquals("bar", result2.getSession().getSessionToken());
    }

    @Test
    public void testInvalidSessionToken() throws InterruptedException {
        ShopMateSession invalid = new ShopMateSession("1", "foo");
        try {
            service.getShoppingListsAsync(invalid).get();
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof ShopMateException);
            ShopMateException cause = (ShopMateException)e.getCause();
            Assert.assertEquals(cause.getCode(), ShopMateErrorCode.INVALID_TOKEN);
        }
    }

    @Test
    public void testCreateShoppingListAsync() throws ExecutionException, InterruptedException {
        ShopMateSession session = service.logInAsync("foo").get().getSession();
        ShoppingList list = service.createShoppingListAsync(session, TestListTitle).get().getList();

        Assert.assertEquals(session.getUserFbid(), list.getCreatorId());
        Assert.assertEquals(TestListTitle, list.getTitle());
        Assert.assertTrue(list.getMemberIds().contains(session.getUserFbid()));
        Assert.assertEquals(0, list.getItemIds().size());
    }

    @Test
    public void testCreateAndGetShoppingList() throws ExecutionException, InterruptedException {
        ShopMateSession session = service.logInAsync("foo").get().getSession();
        CreateShoppingListResult result = service.createShoppingListAsync(session, TestListTitle).get();
        long id = result.getId();

        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync(session).get();
        Assert.assertEquals(1, lists.size());
        Assert.assertTrue(lists.containsKey(id));

        ShoppingList list = lists.get(id);
        Assert.assertEquals(session.getUserFbid(), list.getCreatorId());
        Assert.assertEquals(TestListTitle, list.getTitle());
        Assert.assertTrue(list.getMemberIds().contains(session.getUserFbid()));
        Assert.assertEquals(0, list.getItemIds().size());
    }

    @Test
    public void testCreateAndGetMultipleShoppingLists() throws ExecutionException, InterruptedException {
        ShopMateSession session = service.logInAsync("foo").get().getSession();
        CreateShoppingListResult result1 = service.createShoppingListAsync(session, TestListTitle).get();
        CreateShoppingListResult result2 = service.createShoppingListAsync(session, TestListTitle).get();

        Assert.assertNotEquals(result1.getId(), result2.getId());

        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync(session).get();
        Assert.assertEquals(2, lists.size());
        Assert.assertTrue(lists.containsKey(result1.getId()));
        Assert.assertTrue(lists.containsKey(result2.getId()));
    }
}
