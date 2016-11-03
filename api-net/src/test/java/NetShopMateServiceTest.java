import com.shopmate.api.ShopMateService;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemBuilder;
import com.shopmate.api.model.item.ShoppingListItemHandle;
import com.shopmate.api.model.item.ShoppingListItemPriority;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.list.ShoppingListInvite;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllInvitesResult;
import com.shopmate.api.model.result.GetAllShoppingListsResult;
import com.shopmate.api.model.result.SendInviteResult;
import com.shopmate.api.net.NetShopMateService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class NetShopMateServiceTest {

    // Set this to a token from a test user for testing to work...
    // TODO: Create a dummy token on the server or something which always validates?
    private static final String TestToken = "EAAQZAwjV0NNsBABSykBY7svBtKNXaVszqzF5MefJOuK45R4No03QdTZCRZB5i36lladoTGZB9lWmNAgY16OONxcUEFE6z573gRogRTnLUb1wO5lNsNFWMvkWufWSGZC7YK2BZAmRyzJ1uMwIHnVYsd20ZCZA0F1KxDmwiQq7jE1XbUS2MAlU67WK";
    private static final String TestId = "136682413460238";

    private static final String TestToken2 = "EAAQZAwjV0NNsBAEtywCse4WFZBmVPVgF3xVNvmZAC0HnkhPncH871ZA7nMYwTfVTnQZBKaGbtduCWZBCy21E7ZC7cQRdKqpHJEjKP77FOX2xsm1qhIVMXJ1rFHZAMdZAwrc4qFJnilJLKDKqJEkS3FfxjEZCBpHJAiZAu1qxviCbmeHwBmEN8vf5u2T";
    private static final String TestId2 = "132318773909242";

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

    @Test
    public void testGettingAllInvites() throws ExecutionException, InterruptedException {
        checkInvites(TestToken, TestId);
        checkInvites(TestToken2, TestId2);
    }

    private void checkInvites(String fbToken, String fbid) throws ExecutionException, InterruptedException {
        GetAllInvitesResult allInvites = service.getAllInvites(fbToken).get();
        for (ShoppingListInvite invite : allInvites.getIncomingInvites()) {
            Assert.assertEquals(fbid, invite.getReceiverId());
        }
        for (ShoppingListInvite invite : allInvites.getOutgoingInvites()) {
            Assert.assertEquals(fbid, invite.getSenderId());
        }
    }

    @Test
    public void testSendingAndGettingInvite() throws ExecutionException, InterruptedException {
        // make sure test user 2 has an ID
        service.getAllListsNoItemsAsync(TestToken2).get();

        CreateShoppingListResult createdList = service.createListAsync(TestToken, TestListName).get();
        SendInviteResult sentInvite = service.sendInvite(TestToken, createdList.getId(), TestId2).get();

        GetAllInvitesResult allInvites1 = service.getAllInvites(TestToken).get();
        boolean foundOutgoing1 = false, foundIncoming1 = false;
        for (ShoppingListInvite invite : allInvites1.getOutgoingInvites()) {
            if (invite.getId() == sentInvite.getId()) {
                foundOutgoing1 = true;
                Assert.assertEquals(invite.getListTitle(), TestListName);
                Assert.assertEquals(invite.getSenderId(), TestId);
                Assert.assertEquals(invite.getReceiverId(), TestId2);
                break;
            }
        }
        for (ShoppingListInvite invite : allInvites1.getIncomingInvites()) {
            if (invite.getId() == sentInvite.getId()) {
                foundIncoming1 = true;
                break;
            }
        }
        Assert.assertTrue(foundOutgoing1);
        Assert.assertFalse(foundIncoming1);

        GetAllInvitesResult allInvites2 = service.getAllInvites(TestToken2).get();
        boolean foundOutgoing2 = false, foundIncoming2 = false;
        for (ShoppingListInvite invite : allInvites2.getIncomingInvites()) {
            if (invite.getId() == sentInvite.getId()) {
                foundIncoming2 = true;
                Assert.assertEquals(invite.getListTitle(), TestListName);
                Assert.assertEquals(invite.getSenderId(), TestId);
                Assert.assertEquals(invite.getReceiverId(), TestId2);
                break;
            }
        }
        for (ShoppingListInvite invite : allInvites2.getOutgoingInvites()) {
            if (invite.getId() == sentInvite.getId()) {
                foundOutgoing2 = true;
                break;
            }
        }
        Assert.assertTrue(foundIncoming2);
        Assert.assertFalse(foundOutgoing2);
    }
}
