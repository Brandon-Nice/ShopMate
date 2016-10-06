package com.shopmate.api.net;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.reflect.TypeToken;
import com.shopmate.api.ShopMateErrorCode;
import com.shopmate.api.ShopMateException;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.LogInResult;
import com.shopmate.api.net.model.request.AllListsRequest;
import com.shopmate.api.net.model.request.CreateListRequest;
import com.shopmate.api.net.model.request.LogInRequest;
import com.shopmate.api.net.model.response.AllListsResponse;
import com.shopmate.api.net.model.response.ApiResponse;
import com.shopmate.api.net.model.response.ErrorCodes;
import com.shopmate.api.net.model.response.LogInResponse;
import com.shopmate.api.net.model.response.ShoppingListResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class NetShopMateService implements ShopMateService {
    private static final String BaseUrl = "http://45.55.87.46/api/";

    private static final String LoginUrl = "/login";
    private static final String CreateListUrl = "/list/create";
    private static final String AllListsUrl = "/list/all";

    private static ListeningExecutorService ThreadPool =
            MoreExecutors.listeningDecorator(
                    MoreExecutors.getExitingExecutorService(
                            (ThreadPoolExecutor)Executors.newCachedThreadPool()));

    private final JsonEndpoint endpoint;

    public NetShopMateService() {
        try {
            endpoint = new JsonEndpoint(BaseUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ListenableFuture<LogInResult> logInAsync(final String fbToken) {
        return ThreadPool.submit(new Callable<LogInResult>() {
            @Override
            public LogInResult call() throws ShopMateException {
                LogInRequest request = new LogInRequest(fbToken);
                Type responseType = new TypeToken<ApiResponse<LogInResponse>>(){}.getType();
                ApiResponse<LogInResponse> response = post(LoginUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toLogInResult();
            }
        });
    }

    @Override
    public ListenableFuture<CreateShoppingListResult> createShoppingListAsync(final ShopMateSession session, final String title) {
        return ThreadPool.submit(new Callable<CreateShoppingListResult>() {
            @Override
            public CreateShoppingListResult call() throws Exception {
                CreateListRequest request = new CreateListRequest(session.getSessionToken(), title);
                Type responseType = new TypeToken<ApiResponse<ShoppingListResponse>>(){}.getType();
                ApiResponse<ShoppingListResponse> response = post(CreateListUrl, request, responseType);
                throwIfRequestFailed(response);
                ShoppingListResponse result = response.getResult().get();
                return new CreateShoppingListResult(result.getId(), result.toShoppingList());
            }
        });
    }

    @Override
    public ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync(final ShopMateSession session) {
        return ThreadPool.submit(new Callable<ImmutableMap<Long, ShoppingList>>() {
            @Override
            public ImmutableMap<Long, ShoppingList> call() throws Exception {
                AllListsRequest request = new AllListsRequest(session.getSessionToken());
                Type responseType = new TypeToken<ApiResponse<AllListsResponse>>(){}.getType();
                ApiResponse<AllListsResponse> response = post(AllListsUrl, request, responseType);
                throwIfRequestFailed(response);
                return response.getResult().get().toShoppingListMap();
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