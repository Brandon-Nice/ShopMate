# ShopMate Client API Integration Notes

The API is split between two modules: `api-iface` and `api-mock`. The `api-iface` module holds the public interface of the API, and the `api-mock` module is a mock implementation which doesn't perform any network communications. You will want to link against both modules to start. Once we have an actual implementation of the API ready, we just have to switch out `api-mock` with the new one and everything will still work consistently.

The library aims to be asynchronous so that you don't have to deal with threading too much. Most objects are immutable so that they can safely be passed across thread boundaries. [Guava](https://github.com/google/guava) is used to help achieve this. You'll need to become familiar with how the Guava concurrency module works if you aren't already. Most API calls will return immediately with a `ListenableFuture` object. This is a good starting resource to learn about how those work: https://github.com/google/guava/wiki/ListenableFutureExplained

The main object you'll be using is [ShopMateService](https://github.com/nicebpurdue/ShopMate/blob/master/api-iface/src/main/java/com/shopmate/api/ShopMateService.java), which wraps all of the available API calls. Use `ShopMateServiceProvider.get()` to acquire one as a singleton. Some API calls require a [ShopMateSession](https://github.com/nicebpurdue/ShopMate/blob/master/api-iface/src/main/java/com/shopmate/api/ShopMateSession.java) object, which holds information about a logged-in user. You can get this from the [LogInResult](https://github.com/nicebpurdue/ShopMate/blob/master/api-iface/src/main/java/com/shopmate/api/model/result/LogInResult.java) returned by the `logInAsync()` method.

The pattern you'll use to submit API calls from the UI will probably look something like this:

```Java
// Get the ShopMateService singleton
ShopMateService service = ShopMateServiceProvider.get();

// Start an API call in the background
ListenableFuture<LogInResult> future = service.logInAsync(fbToken);

// Register callbacks to run on the main thread once the API call completes
Futures.addCallback(future, new FutureCallback<LogInResult>() {
	public void onSuccess(LogInResult result) {
		// API call completed successfully, process the result here
	}
	public void onFailure(Throwable t) {
		// API call failed, process the exception here
		// t can be a ShopMateException if the error is ShopMate-related
	}
}, TaskExecutors.MAIN_THREAD);
```

(note that the [TaskExecutors.MAIN_THREAD](https://developers.google.com/android/reference/com/google/android/gms/tasks/TaskExecutors#MAIN_THREAD) object is from Google Play services, which we'll likely need anyway)