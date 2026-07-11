package com.pompom.group6.network;

import com.pompom.group6.network.dto.ApiAddress;
import com.pompom.group6.network.dto.ApiOrder;
import com.pompom.group6.network.dto.ApiPointsTransaction;
import com.pompom.group6.network.dto.ApiProduct;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.network.dto.AddressRequest;
import com.pompom.group6.network.dto.AuthDtos;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Khai báo các endpoint của backend. Bắt đầu với Auth; sẽ mở rộng dần khi migrate
 * từng màn (users, products, orders, cart, community...).
 */
public interface ApiService {

    // ---- Auth ----
    @POST("api/auth/login")
    Call<ApiUser> login(@Body AuthDtos.LoginRequest body);

    @POST("api/auth/register")
    Call<ApiUser> register(@Body AuthDtos.RegisterRequest body);

    // ---- Users / Profile ----
    @GET("api/users/{id}")
    Call<ApiUser> getUser(@Path("id") String userId);

    @PUT("api/users/{id}")
    Call<ApiUser> updateUser(@Path("id") String userId,
                             @Body com.pompom.group6.network.dto.UserUpdateRequest body);

    @POST("api/users/{id}/change-password")
    Call<Void> changePassword(@Path("id") String userId,
                              @Body com.pompom.group6.network.dto.ChangePasswordRequest body);

    /** Danh sách địa chỉ giao hàng. */
    @GET("api/users/{id}/addresses")
    Call<List<ApiAddress>> getAddresses(@Path("id") String userId);

    @POST("api/users/{id}/addresses")
    Call<ApiAddress> addAddress(@Path("id") String userId, @Body AddressRequest body);

    @PUT("api/users/{id}/addresses/{addrId}/default")
    Call<Void> setDefaultAddress(@Path("id") String userId, @Path("addrId") String addrId);

    @DELETE("api/users/{id}/addresses/{addrId}")
    Call<Void> deleteAddress(@Path("id") String userId, @Path("addrId") String addrId);

    /** Theo dõi / bỏ theo dõi người dùng khác — tạo thông báo thật cho người được theo dõi. */
    @GET("api/users/{id}/follow-status")
    Call<Map<String, Boolean>> getFollowStatus(@Path("id") String userId, @Query("follower_id") String followerId);

    @POST("api/users/{id}/follow")
    Call<Map<String, Boolean>> followUser(@Path("id") String userId, @Body Map<String, String> body);

    @DELETE("api/users/{id}/follow")
    Call<Map<String, Boolean>> unfollowUser(@Path("id") String userId, @Query("follower_id") String followerId);

    /** Voucher của user. */
    @GET("api/users/{id}/vouchers")
    Call<List<com.pompom.group6.network.dto.ApiVoucher>> getUserVouchers(@Path("id") String userId);

    /** Tất cả voucher đang hoạt động. */
    @GET("api/vouchers")
    Call<List<com.pompom.group6.network.dto.ApiVoucher>> getVouchers();

    /** Lưu voucher cho user. */
    @POST("api/users/{id}/vouchers/{voucherId}")
    Call<Void> saveVoucher(@Path("id") String userId, @Path("voucherId") String voucherId);

    /** Danh sách sản phẩm yêu thích. */
    @GET("api/users/{id}/wishlist")
    Call<List<ApiProduct>> getWishlist(@Path("id") String userId);

    /** Thêm vào yêu thích. Body: { product_id } */
    @POST("api/users/{id}/wishlist")
    Call<Void> addToWishlist(@Path("id") String userId,
                             @Body java.util.Map<String, String> body);

    /** Xóa khỏi yêu thích. */
    @DELETE("api/users/{id}/wishlist/{productId}")
    Call<Void> removeFromWishlist(@Path("id") String userId,
                                  @Path("productId") String productId);

    /** Đếm đơn theo trạng thái: {status: count}. */
    @GET("api/orders/counts")
    Call<Map<String, Integer>> getOrderCounts(@Query("user_id") String userId);

    /** Lịch sử điểm của user. */
    @GET("api/users/{id}/points")
    Call<List<ApiPointsTransaction>> getUserPoints(@Path("id") String userId);

    /** Đánh giá do user đã viết (kèm tên & ảnh sản phẩm). */
    @GET("api/users/{id}/reviews")
    Call<List<com.pompom.group6.network.dto.ApiMyReview>> getUserReviews(@Path("id") String userId);

    /** Phân tích "vấn đề da quan tâm" từ bài Community user đã tương tác. */
    @GET("api/users/{id}/skin-concerns-analysis")
    Call<com.pompom.group6.network.dto.ApiSkinAnalysis> getSkinConcernAnalysis(@Path("id") String userId);

    // ---- Orders ----
    @GET("api/orders")
    Call<List<ApiOrder>> getOrders(@Query("user_id") String userId);

    @GET("api/orders/{id}")
    Call<com.pompom.group6.network.dto.ApiOrderDetail> getOrder(@Path("id") String orderId);

    @POST("api/orders")
    Call<ApiOrder> createOrder(@Body com.pompom.group6.network.dto.OrderRequest body);

    @GET("api/orders")
    Call<List<ApiOrder>> getOrdersByPhone(@Query("phone") String phone);

    @GET("api/orders/meta")
    Call<com.pompom.group6.network.dto.ApiOrderMeta> getOrderMeta();

    // ---- Cart (đồng bộ đa thiết bị) ----
    @GET("api/carts")
    Call<com.pompom.group6.network.dto.ApiCart> getCart(@Query("user_id") String userId);

    @POST("api/carts/items")
    Call<Void> addCartItem(@Body com.pompom.group6.network.dto.CartItemRequest body);

    @PUT("api/carts/set")
    Call<Void> setCartQuantity(@Body com.pompom.group6.network.dto.CartItemRequest body);

    @DELETE("api/carts/by-product")
    Call<Void> removeCartByProduct(@Query("user_id") String userId, @Query("product_id") String productId);

    @DELETE("api/carts")
    Call<Void> clearCart(@Query("user_id") String userId);

    // ---- Products ----
    @GET("api/products")
    Call<List<ApiProduct>> getProducts();

    /** Danh sách sản phẩm có lọc/sắp xếp (tham số null sẽ bị bỏ qua). category_id có thể là nhiều id nối bằng dấu phẩy. */
    @GET("api/products")
    Call<List<ApiProduct>> getProductsFiltered(@Query("category_id") String categoryIds,
                                               @Query("min_price") Long minPrice,
                                               @Query("max_price") Long maxPrice,
                                               @Query("min_rating") Float minRating,
                                               @Query("sort") String sort);

    @GET("api/products")
    Call<List<ApiProduct>> searchProducts(@Query("q") String query);

    @GET("api/products/{id}")
    Call<ApiProduct> getProduct(@Path("id") String productId);

    @GET("api/products/{id}/reviews")
    Call<List<com.pompom.group6.network.dto.ApiReview>> getProductReviews(@Path("id") String productId);

    @POST("api/products/{id}/reviews")
    Call<com.pompom.group6.network.dto.ApiReview> submitReview(@Path("id") String productId,
                                                               @Body java.util.Map<String, Object> body);

    @GET("api/products/{id}/related")
    Call<List<ApiProduct>> getRelatedProducts(@Path("id") String productId);

    /** Gợi ý sản phẩm theo loại da (oily/dry/combination/sensitive/normal) — có thể trả về rỗng thật. */
    @GET("api/products/by-skin-type/{type}")
    Call<List<ApiProduct>> getProductsBySkinType(@Path("type") String skinType);

    @GET("api/banners")
    Call<List<com.pompom.group6.network.dto.ApiBanner>> getBanners();

    @GET("api/categories")
    Call<List<com.pompom.group6.network.dto.ApiCategory>> getCategories();

    @GET("api/flash-sale")
    Call<List<com.pompom.group6.network.dto.ApiFlashSaleProduct>> getFlashSale(@Query("limit") Integer limit);

    @GET("api/promotions")
    Call<List<com.pompom.group6.network.dto.ApiProduct>> getPromotions();

    // ---- Community: Reels / Blog / Tips từ chuyên gia ----
    @GET("api/reels")
    Call<List<com.pompom.group6.network.dto.ApiReel>> getReels(@Query("limit") Integer limit);

    @GET("api/blogs")
    Call<List<com.pompom.group6.network.dto.ApiBlog>> getBlogs(@Query("limit") Integer limit);

    @GET("api/blogs/{id}")
    Call<com.pompom.group6.network.dto.ApiBlog> getBlog(@Path("id") String id);

    @GET("api/experts")
    Call<List<com.pompom.group6.network.dto.ApiExpert>> getExperts();

    @GET("api/expert-articles")
    Call<List<com.pompom.group6.network.dto.ApiExpertArticle>> getExpertArticles(@Query("limit") Integer limit);

    @GET("api/expert-articles/{id}")
    Call<com.pompom.group6.network.dto.ApiExpertArticle> getExpertArticle(@Path("id") String id);

    @POST("api/consultation-requests")
    Call<Void> submitConsultationRequest(@Body com.pompom.group6.network.dto.ConsultationRequestBody body);

    // ---- Community: Story 24h theo bán kính GPS ----
    @GET("api/nearby-posts")
    Call<List<com.pompom.group6.network.dto.ApiNearbyPost>> getNearbyPosts(@Query("lat") double lat,
                                                                            @Query("lng") double lng,
                                                                            @Query("radius_km") Integer radiusKm);

    /** Story (nearby-post 24h) do một user đã đăng — cho màn "Story đã đăng". */
    @GET("api/nearby-posts")
    Call<List<com.pompom.group6.network.dto.ApiNearbyPost>> getUserStories(@Query("user_id") String userId);

    @POST("api/nearby-posts")
    Call<com.pompom.group6.network.dto.ApiNearbyPost> createNearbyPost(@Body com.pompom.group6.network.dto.NearbyPostRequest body);

    @GET("api/notifications")
    Call<List<com.pompom.group6.network.dto.ApiNotification>> getNotifications(@Query("user_id") String userId);

    // ---- Community ----
    @GET("api/community/posts")
    Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> getCommunityPosts(@Query("limit") Integer limit,
                                                                                 @Query("q") String query);

    /** author_id → tab "Của bạn"; saved_by → tab "Đã lưu"; viewer_id → gắn is_saved/is_liked đúng người xem. */
    @GET("api/community/posts")
    Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> getCommunityPostsFiltered(
            @Query("limit") Integer limit,
            @Query("author_id") String authorId,
            @Query("saved_by") String savedBy,
            @Query("viewer_id") String viewerId);

    @POST("api/community/posts/{id}/save")
    Call<java.util.Map<String, Boolean>> toggleSavePost(@Path("id") String postId,
                                                        @Body java.util.Map<String, String> body);

    /** Ẩn bài viết khỏi feed của riêng người dùng hiện tại (không xoá bài, không ảnh hưởng người khác). */
    @POST("api/community/posts/{id}/hide")
    Call<java.util.Map<String, Boolean>> hidePost(@Path("id") String postId,
                                                  @Body java.util.Map<String, String> body);

    @GET("api/community/highlights")
    Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> getCommunityHighlights(@Query("limit") Integer limit);

    @GET("api/community/posts/{id}")
    Call<com.pompom.group6.network.dto.ApiCommunityPost> getCommunityPost(@Path("id") String postId);

    @GET("api/community/posts/{id}/comments")
    Call<List<com.pompom.group6.network.dto.ApiComment>> getPostComments(@Path("id") String postId);

    @POST("api/community/posts/{id}/comments")
    Call<com.pompom.group6.network.dto.ApiComment> addComment(@Path("id") String postId,
                                                              @Body java.util.Map<String, String> body);

    @POST("api/community/posts/{id}/like")
    Call<java.util.Map<String, Boolean>> toggleLike(@Path("id") String postId,
                                                    @Body java.util.Map<String, String> body);

    /** Tăng lượt chia sẻ thật (không toggle) — trả về { share_count }. */
    @POST("api/community/posts/{id}/share")
    Call<java.util.Map<String, Integer>> sharePost(@Path("id") String postId);

    @GET("api/community/posts/{id}/tagged")
    Call<List<ApiProduct>> getPostTaggedProducts(@Path("id") String postId);

    @POST("api/community/posts")
    Call<com.pompom.group6.network.dto.ApiCommunityPost> createPost(@Body com.pompom.group6.network.dto.CreatePostRequest body);
}
