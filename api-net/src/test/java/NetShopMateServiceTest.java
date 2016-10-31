import com.google.common.collect.ImmutableMap;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.net.NetShopMateService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NetShopMateServiceTest {

    // Set this to a token from a test user for testing to work...
    // TODO: Create a dummy token on the server or something which always validates?
    private static final String TestToken = "EAAQZAwjV0NNsBAKHmMLVfmE29qxjAQOhHJO5PXocvPk5NOwbAOCgZC7JaLJ63ZAQTwvaHhGE37KGGSalv9OUjyd8QGemd0EuwcebkkiXeI3pZB277ZBplhGgwwtXvdruogqBwZBOx773A4JirAh0pHdbWgzDRkzDMiCJkCWAh9X0dQhE2gNolU";
    private static final String TestId = "136682413460238";

    private static final String TestListName = "Test List";

    private ShopMateService service;

    @Before
    public void before() {
        service = new NetShopMateService();
    }

    @Test
    public void testCreateShoppingListAsync() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result = service.createShoppingListAsync(TestToken, TestListName).get();
        Assert.assertEquals(result.getList().getTitle(), TestListName);
        Assert.assertEquals(result.getList().getCreatorId(), TestId);
        Assert.assertTrue(result.getList().getMemberIds().contains(TestId));
    }

    @Test
    public void testGetShoppingListsAsync() throws ExecutionException, InterruptedException {
        ImmutableMap<Long, ShoppingList> lists = service.getShoppingListsAsync(TestToken).get();
        for (ShoppingList list : lists.values()) {
            Assert.assertTrue(list.getMemberIds().contains(TestId));
        }
    }
}
