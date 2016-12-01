package com.shopmate.api.net;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.reflect.TypeToken;
import com.shopmate.api.ShopMateErrorCode;
import com.shopmate.api.ShopMateException;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllInvitesResult;
import com.shopmate.api.model.result.GetAllShoppingListsResult;
import com.shopmate.api.model.result.SendInviteResult;
import com.shopmate.api.net.model.list.ShoppingListJson;
import com.shopmate.api.net.model.request.AcceptInviteRequest;
import com.shopmate.api.net.model.request.CancelInviteRequest;
import com.shopmate.api.net.model.request.CreateItemRequest;
import com.shopmate.api.net.model.request.CreateListRequest;
import com.shopmate.api.net.model.request.DeclineInviteRequest;
import com.shopmate.api.net.model.request.GetAllInvitesRequest;
import com.shopmate.api.net.model.request.GetAllListsRequest;
import com.shopmate.api.net.model.request.GetListRequest;
import com.shopmate.api.net.model.request.RegisterFcmTokenRequest;
import com.shopmate.api.net.model.request.LeaveListRequest;
import com.shopmate.api.net.model.request.SendInviteRequest;
import com.shopmate.api.net.model.response.ApiResponse;
import com.shopmate.api.net.model.response.CreateItemResponse;
import com.shopmate.api.net.model.response.ErrorCodes;
import com.shopmate.api.net.model.response.GetAllInvitesResponse;
import com.shopmate.api.net.model.response.GetAllListsResponse;
import com.shopmate.api.net.model.response.GetItemResponse;
import com.shopmate.api.net.model.response.GetListResponse;
import com.shopmate.api.net.model.response.SendInviteResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NetShopMateService implements ShopMateService {
    private static final String BaseUrl = "http://45.55.87.46/api/";

    private static final String CreateListUrl = "/list/create";
    private static final String AllListsUrl = "/list/all";
    private static final String GetListUrl = "/list/%s";
    private static final String LeaveListUrl = "/list/%s/leave";

    private static final String CreateItemUrl = "/item/create";
    private static final String GetItemUrl = "/item/%s";

    private static final String AllInvitesUrl = "/invite/all";
    private static final String SendInviteUrl = "/invite/send";
    private static final String AcceptInviteUrl = "/invite/%s/accept";
    private static final String DeclineInviteUrl = "/invite/%s/decline";
    private static final String CancelInviteUrl = "/invite/%s/cancel";

    private static final String RegisterNotifyUrl = "/notify/register";

    private static ListeningExecutorService ThreadPool =
            MoreExecutors.listeningDecorator(
                    MoreExecutors.getExitingExecutorService(
                            (ThreadPoolExecutor)Executors.newCachedThreadPool()));

    private final JsonEndpoint endpoint;

    public NetShopMateService() {
        this(BaseUrl);
    }

    public NetShopMateService(String baseUrl) {
        try {
            endpoint = new JsonEndpoint(baseUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListenableFuture<CreateShoppingListResult> createListAsync(final String fbToken, final String title, final ImmutableSet<String> inviteUserIds) {
        return ThreadPool.submit(new Callable<CreateShoppingListResult>() {
            @Override
            public CreateShoppingListResult call() throws Exception {
                CreateListRequest request = new CreateListRequest(fbToken, title, inviteUserIds);
                Type responseType = new TypeToken<ApiResponse<ShoppingListJson>>(){}.getType();
                ApiResponse<ShoppingListJson> response = post(CreateListUrl, request, responseType);
                throwIfRequestFailed(response);
                ShoppingListJson result = response.getResult().get();
                return new CreateShoppingListResult(result.getId(), result.toShoppingList());
            }
        });
    }

    @Override
    public ListenableFuture<Void> leaveListAsync(final String fbToken, final long listId) {
        return ThreadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                LeaveListRequest request = new LeaveListRequest(fbToken);
                String url = String.format(LeaveListUrl, listId);
                Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<ShoppingList> getListAndItemsAsync(final String fbToken, final long listId) {
        return ThreadPool.submit(new Callable<ShoppingList>() {
            @Override
            public ShoppingList call() throws Exception {
                GetListRequest request = new GetListRequest(fbToken);
                String url = String.format(GetListUrl, listId);
                Type responseType = new TypeToken<ApiResponse<GetListResponse>>(){}.getType();
                ApiResponse<GetListResponse> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toShoppingList();
            }
        });
    }

    @Override
    public ListenableFuture<GetAllShoppingListsResult> getAllListsNoItemsAsync(String fbToken) {
        return getAllShoppingListsInternal(fbToken, false);
    }

    @Override
    public ListenableFuture<GetAllShoppingListsResult> getAllListsAndItemsAsync(String fbToken) {
        return getAllShoppingListsInternal(fbToken, true);
    }

    private ListenableFuture<GetAllShoppingListsResult> getAllShoppingListsInternal(final String fbToken, final boolean withItems) {
        return ThreadPool.submit(new Callable<GetAllShoppingListsResult>() {
            @Override
            public GetAllShoppingListsResult call() throws Exception {
                GetAllListsRequest request = new GetAllListsRequest(fbToken, withItems);
                Type responseType = new TypeToken<ApiResponse<GetAllListsResponse>>(){}.getType();
                ApiResponse<GetAllListsResponse> response = post(AllListsUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toResult();
            }
        });
    }

    @Override
    public ListenableFuture<CreateShoppingListItemResult> createItemAsync(final String fbToken, final long listId, final ShoppingListItem item) {
        return ThreadPool.submit(new Callable<CreateShoppingListItemResult>() {
            @Override
            public CreateShoppingListItemResult call() throws Exception {
                CreateItemRequest request = new CreateItemRequest(fbToken, listId, item);
                Type responseType = new TypeToken<ApiResponse<CreateItemResponse>>(){}.getType();
                ApiResponse<CreateItemResponse> response = post(CreateItemUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toResult();
            }
        });
    }

    @Override
    public ListenableFuture<ShoppingListItem> getItemAsync(final String fbToken, final long itemId) {
        return ThreadPool.submit(new Callable<ShoppingListItem>() {
            @Override
            public ShoppingListItem call() throws Exception {
                GetListRequest request = new GetListRequest(fbToken);
                String url = String.format(GetItemUrl, itemId);
                Type responseType = new TypeToken<ApiResponse<GetItemResponse>>(){}.getType();
                ApiResponse<GetItemResponse> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toItem();
            }
        });
    }

    @Override
    public ListenableFuture<GetAllInvitesResult> getAllInvites(final String fbToken) {
        return ThreadPool.submit(new Callable<GetAllInvitesResult>() {
            @Override
            public GetAllInvitesResult call() throws Exception {
                GetAllInvitesRequest request = new GetAllInvitesRequest(fbToken);
                Type responseType = new TypeToken<ApiResponse<GetAllInvitesResponse>>(){}.getType();
                ApiResponse<GetAllInvitesResponse> response = post(AllInvitesUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toResult();
            }
        });
    }

    @Override
    public ListenableFuture<SendInviteResult> sendInviteAsync(final String fbToken, final long listId, final String receiverUserId) {
        return ThreadPool.submit(new Callable<SendInviteResult>() {
            @Override
            public SendInviteResult call() throws Exception {
                SendInviteRequest request = new SendInviteRequest(fbToken, listId, receiverUserId);
                Type responseType = new TypeToken<ApiResponse<SendInviteResponse>>(){}.getType();
                ApiResponse<SendInviteResponse> response = post(SendInviteUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toResult();
            }
        });
    }

    @Override
    public ListenableFuture<Void> acceptInviteAsync(final String fbToken, final long inviteId) {
        return ThreadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                AcceptInviteRequest request = new AcceptInviteRequest(fbToken);
                String url = String.format(AcceptInviteUrl, inviteId);
                Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> declineInviteAsync(final String fbToken, final long inviteId) {
        return ThreadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                DeclineInviteRequest request = new DeclineInviteRequest(fbToken);
                String url = String.format(DeclineInviteUrl, inviteId);
                Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> cancelInviteAsync(final String fbToken, final long inviteId) {
        return ThreadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                CancelInviteRequest request = new CancelInviteRequest(fbToken);
                String url = String.format(CancelInviteUrl, inviteId);
                Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> response = post(url, request, responseType);
                throwIfRequestFailed(response);
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> registerFcmTokenAsync(final String fbToken, final String fcmToken) {
        return ThreadPool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                RegisterFcmTokenRequest request = new RegisterFcmTokenRequest(fbToken, fcmToken);
                Type responseType = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> response = post(RegisterNotifyUrl, request, responseType);
                throwIfRequestFailed(response);
                return null;
            }
        });
    }

    private <TRequest, TResponse> TResponse post(String url, TRequest request, Type responseType) throws ShopMateException {
        try {
            return endpoint.post(url, request, responseType);
        } catch (IOException e) {
            throw new ShopMateException(ShopMateErrorCode.CONNECTION_ERROR, e);
        }
    }

    private <TResult> void throwIfRequestFailed(ApiResponse<TResult> response) throws ShopMateException {
        if (!response.getResult().isPresent()) {
            ShopMateErrorCode code = ErrorCodes.getErrorCode(response.getError().orNull());
            String message = ErrorCodes.getErrorMessage(code);
            throw new ShopMateException(code, message);
        }
    }
}