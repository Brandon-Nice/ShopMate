import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.LogInResult;
import com.shopmate.api.net.NetShopMateService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NetShopMateServiceTest {

    // Set this to a token from a test user for testing to work...
    // TODO: Create a dummy token on the server or something which always validates?
    private static final String TestToken = "EAAQZAwjV0NNsBAHIpFiHYPEzvp1hgKj3KvfkDLhkOtaQJZCIfZCy8jP4V8cMnYOBQJOCgA2E4HKMCqJNcNGRgweqNmuv8CzFmqvbOHSimFD8ZCoSPPo3F6xSnqtq3rRd96G03ZBZC9hkZCtZAXFshKUJEZA5OONekZByoN4ECFoePaASdbhV3ajNoZA";
    private static final String TestId = "136682413460238";

    private ShopMateService service;

    @Before
    public void before() {
        service = new NetShopMateService();
    }

    @Test
    public void testLogInAsync() throws ExecutionException, InterruptedException {
        LogInResult result = service.logInAsync(TestToken).get();
        Assert.assertEquals(TestToken, result.getSession().getSessionToken());
        Assert.assertEquals(TestId, result.getSession().getUserFbid());
        for (ShoppingList list : result.getShoppingLists().values()) {
            Assert.assertTrue(list.getMemberIds().contains(TestId));
        }
    }

    @Test
    public void testCreateShoppingListAsync() throws ExecutionException, InterruptedException {
        LogInResult logIn = service.logInAsync(TestToken).get();
        ShopMateSession session = logIn.getSession();
        CreateShoppingListResult result = service.createShoppingListAsync(session, new ShoppingList(
                session.getUserFbid(),
                "Test List",
                ImmutableSet.<String>of(),
                ImmutableSet.<Long>of())).get();
        Assert.assertEquals(result.getList().getTitle(), "Test List");
        Assert.assertEquals(result.getList().getCreatorId(), TestId);
        Assert.assertTrue(result.getList().getMemberIds().contains(TestId));
    }

    @Test
    public void testGetShoppingListsAsync() throws ExecutionException, InterruptedException {
        LogInResult logIn = service.logInAsync(TestToken).get();
        ShopMateSession session = logIn.getSession();
        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync(session).get();
        for (ShoppingList list : lists.values()) {
            Assert.assertTrue(list.getMemberIds().contains(TestId));
        }
    }
}
