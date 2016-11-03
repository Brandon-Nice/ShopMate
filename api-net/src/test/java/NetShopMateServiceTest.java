import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
    private static final String TestListInviteName = "Test List with Invite";

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
        CreateShoppingListResult result = service.createListAsync(TestToken, TestListName, ImmutableSet.<String>of()).get();
        Assert.assertEquals(TestListName, result.getList().getTitle());
        Assert.assertEquals(TestId, result.getList().getCreatorId());
        Assert.assertTrue(result.getList().getMemberIds().contains(TestId));
        Assert.assertEquals(0, result.getList().getItems().size());
    }

    @Test
    public void testCreatingAndGettingList() throws ExecutionException, InterruptedException {
        CreateShoppingListResult result = service.createListAsync(TestToken, TestListName, ImmutableSet.<String>of()).get();
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
        CreateShoppingListResult createListResult = service.createListAsync(TestToken, TestListName, ImmutableSet.<String>of()).get();
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
        CreateShoppingListResult createListResult = service.createListAsync(TestToken, TestListName, ImmutableSet.<String>of()).get();

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

        CreateShoppingListResult createdList = service.createListAsync(TestToken, TestListName, ImmutableSet.<String>of()).get();
        SendInviteResult sentInvite = service.sendInvite(TestToken, createdList.getId(), TestId2).get();
        long inviteId = sentInvite.getId();

        GetAllInvitesResult allInvites1 = service.getAllInvites(TestToken).get();
        int outgoingIndex1 = indexOfInviteById(allInvites1.getOutgoingInvites(), inviteId);
        int incomingIndex1 = indexOfInviteById(allInvites1.getIncomingInvites(), inviteId);
        Assert.assertTrue(outgoingIndex1 >= 0);
        Assert.assertTrue(incomingIndex1 < 0);
        ShoppingListInvite invite1 = allInvites1.getOutgoingInvites().get(outgoingIndex1);
        Assert.assertEquals(invite1.getListTitle(), TestListName);
        Assert.assertEquals(invite1.getSenderId(), TestId);
        Assert.assertEquals(invite1.getReceiverId(), TestId2);

        GetAllInvitesResult allInvites2 = service.getAllInvites(TestToken2).get();
        int outgoingIndex2 = indexOfInviteById(allInvites2.getOutgoingInvites(), inviteId);
        int incomingIndex2 = indexOfInviteById(allInvites2.getIncomingInvites(), inviteId);
        Assert.assertTrue(outgoingIndex2 < 0);
        Assert.assertTrue(incomingIndex2 >= 0);
        ShoppingListInvite invite2 = allInvites2.getIncomingInvites().get(incomingIndex2);
        Assert.assertEquals(invite2.getListTitle(), TestListName);
        Assert.assertEquals(invite2.getSenderId(), TestId);
        Assert.assertEquals(invite2.getReceiverId(), TestId2);
    }

    @Test
    public void testCreatingListWithInvite() throws ExecutionException, InterruptedException {
        // make sure test user 2 has an ID
        service.getAllListsNoItemsAsync(TestToken2).get();

        service.createListAsync(TestToken, TestListInviteName, ImmutableSet.of(TestId2)).get();

        GetAllInvitesResult allInvites1 = service.getAllInvites(TestToken).get();
        int outgoingIndex1 = indexOfInviteByName(allInvites1.getOutgoingInvites(), TestListInviteName);
        int incomingIndex1 = indexOfInviteByName(allInvites1.getIncomingInvites(), TestListInviteName);
        Assert.assertTrue(outgoingIndex1 >= 0);
        Assert.assertTrue(incomingIndex1 < 0);
        ShoppingListInvite invite1 = allInvites1.getOutgoingInvites().get(outgoingIndex1);
        Assert.assertEquals(invite1.getSenderId(), TestId);
        Assert.assertEquals(invite1.getReceiverId(), TestId2);

        GetAllInvitesResult allInvites2 = service.getAllInvites(TestToken2).get();
        int outgoingIndex2 = indexOfInviteByName(allInvites2.getOutgoingInvites(), TestListInviteName);
        int incomingIndex2 = indexOfInviteByName(allInvites2.getIncomingInvites(), TestListInviteName);
        Assert.assertTrue(outgoingIndex2 < 0);
        Assert.assertTrue(incomingIndex2 >= 0);
        ShoppingListInvite invite2 = allInvites2.getIncomingInvites().get(incomingIndex2);
        Assert.assertEquals(invite2.getSenderId(), TestId);
        Assert.assertEquals(invite2.getReceiverId(), TestId2);
    }

    private static int indexOfInviteById(ImmutableList<ShoppingListInvite> invites, long id) {
        for (int i = 0; i < invites.size(); i++) {
            if (invites.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOfInviteByName(ImmutableList<ShoppingListInvite> invites, String name) {
        for (int i = 0; i < invites.size(); i++) {
            if (invites.get(i).getListTitle().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
