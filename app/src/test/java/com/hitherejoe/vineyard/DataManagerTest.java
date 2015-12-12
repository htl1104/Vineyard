package com.hitherejoe.vineyard;


import com.hitherejoe.vineyard.data.DataManager;
import com.hitherejoe.vineyard.data.local.PreferencesHelper;
import com.hitherejoe.vineyard.data.model.Authentication;
import com.hitherejoe.vineyard.data.model.Post;
import com.hitherejoe.vineyard.data.model.Tag;
import com.hitherejoe.vineyard.data.model.User;
import com.hitherejoe.vineyard.data.remote.VineyardService;
import com.hitherejoe.vineyard.test.common.MockModelFabric;
import com.hitherejoe.vineyard.ui.fragment.SearchFragment;
import com.hitherejoe.vineyard.util.DefaultConfig;
import com.hitherejoe.vineyard.util.MockModelsUtil;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class DataManagerTest {

    private DataManager mDataManager;
    private VineyardService mMockVineyardService;

    @Before
    public void setUp() {
        mMockVineyardService = mock(VineyardService.class);
        mDataManager = new DataManager(mMockVineyardService,
                mock(Bus.class),
                new PreferencesHelper(RuntimeEnvironment.application),
                Schedulers.immediate());
    }

    @Test
    public void shouldGetAccessToken() throws Exception {
        Authentication mockAuthentication = MockModelsUtil.createMockSuccessAuthentication();
        when(mMockVineyardService.getAccessToken(anyString(), anyString()))
                .thenReturn(Observable.just(mockAuthentication));

        TestSubscriber<Authentication> result = new TestSubscriber<>();
        mDataManager.getAccessToken("", "").subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockAuthentication);

        assertEquals(mockAuthentication.data.key, mDataManager.getPreferencesHelper().getAccessToken());
    }

    @Test
    public void shouldFailGetAccessToken() throws Exception {
        Authentication mockAuthentication = MockModelsUtil.createMockErrorAuthentication();
        when(mMockVineyardService.getAccessToken(anyString(), anyString()))
                .thenReturn(Observable.just(mockAuthentication));

        TestSubscriber<Authentication> result = new TestSubscriber<>();
        mDataManager.getAccessToken("", "").subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockAuthentication);

        assertEquals(mDataManager.getPreferencesHelper().getAccessToken(), null);
    }

    @Test
    public void shouldGetSignedInUser() throws Exception {
        User mockUser = MockModelsUtil.createMockUser();
        when(mMockVineyardService.getSignedInUser())
                .thenReturn(Observable.just(mockUser));

        TestSubscriber<User> result = new TestSubscriber<>();
        mDataManager.getSignedInUser().subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockUser);
    }

    @Test
    public void shouldGetSignedInUserById() throws Exception {
        User mockUser = MockModelsUtil.createMockUser();
        when(mMockVineyardService.getUser(mockUser.userId))
                .thenReturn(Observable.just(mockUser));

        TestSubscriber<User> result = new TestSubscriber<>();
        mDataManager.getUser(mockUser.userId).subscribe(result);
        result.assertNoErrors();
        result.assertValue(mockUser);
    }

    @Test
    public void shouldGetPopularPosts() throws Exception {
        List<Post> mockPostLists = MockModelsUtil.createMockListOfPosts(20);
        VineyardService.PostResponse popularResponse = new VineyardService.PostResponse();
        popularResponse.data = new VineyardService.PostResponse.Data();
        popularResponse.data.records = mockPostLists;
        when(mMockVineyardService.getPopularPosts(anyInt(), anyString()))
                .thenReturn(Observable.just(popularResponse));

        TestSubscriber<VineyardService.PostResponse> result = new TestSubscriber<>();
        mDataManager.getPopularPosts(0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(popularResponse);
    }

    @Test
    public void shouldGetEditorsPicksPosts() throws Exception {
        List<Post> mockPostLists = MockModelsUtil.createMockListOfPosts(20);
        VineyardService.PostResponse editorsPicksResponse = new VineyardService.PostResponse();
        editorsPicksResponse.data = new VineyardService.PostResponse.Data();
        editorsPicksResponse.data.records = mockPostLists;
        when(mMockVineyardService.getEditorsPicksPosts(anyInt(), anyString()))
                .thenReturn(Observable.just(editorsPicksResponse));

        TestSubscriber<VineyardService.PostResponse> result = new TestSubscriber<>();
        mDataManager.getEditorsPicksPosts(0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(editorsPicksResponse);
    }

    @Test
    public void shouldGetPostsByTag() throws Exception {
        List<Post> mockPostLists = MockModelsUtil.createMockListOfPosts(20);
        VineyardService.PostResponse editorsPicksResponse = new VineyardService.PostResponse();
        editorsPicksResponse.data = new VineyardService.PostResponse.Data();
        editorsPicksResponse.data.records = mockPostLists;
        when(mMockVineyardService.getPostsByTag(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(editorsPicksResponse));

        TestSubscriber<VineyardService.PostResponse> result = new TestSubscriber<>();
        mDataManager.getPostsByTag("tag", 0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(editorsPicksResponse);
    }

    @Test
    public void shouldGetPostsByUser() throws Exception {
        List<Post> mockPostLists = MockModelsUtil.createMockListOfPosts(20);
        VineyardService.PostResponse editorsPicksResponse = new VineyardService.PostResponse();
        editorsPicksResponse.data = new VineyardService.PostResponse.Data();
        editorsPicksResponse.data.records = mockPostLists;
        when(mMockVineyardService.getUserTimeline(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(editorsPicksResponse));

        TestSubscriber<VineyardService.PostResponse> result = new TestSubscriber<>();
        mDataManager.getPostsByUser("userId", 0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(editorsPicksResponse);
    }

    @Test
    public void shouldGetTagsByKeyword() throws Exception {
        String tag = "tag";

        List<Tag> mockPostLists = MockModelFabric.createMockListOfTags(20, tag);
        VineyardService.TagResponse editorsPicksResponse = new VineyardService.TagResponse();
        editorsPicksResponse.data = new VineyardService.TagResponse.Data();
        editorsPicksResponse.data.records = mockPostLists;
        when(mMockVineyardService.searchByTag(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(editorsPicksResponse));

        TestSubscriber<VineyardService.TagResponse> result = new TestSubscriber<>();
        mDataManager.searchByTag(tag, 0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(editorsPicksResponse);
    }

    @Test
    public void shouldGetUsersByKeyword() throws Exception {
        List<User> mockPostLists = MockModelFabric.createMockListOfUsers(20);
        VineyardService.UserResponse editorsPicksResponse = new VineyardService.UserResponse();
        editorsPicksResponse.data = new VineyardService.UserResponse.Data();
        editorsPicksResponse.data.records = mockPostLists;
        when(mMockVineyardService.searchByUser(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(editorsPicksResponse));

        TestSubscriber<VineyardService.UserResponse> result = new TestSubscriber<>();
        mDataManager.searchByUser("userId", 0, "anchor").subscribe(result);
        result.assertNoErrors();
        result.assertValue(editorsPicksResponse);
    }

    @Test
    public void shouldSearch() throws Exception {
        String tag = "tag";
        String userId = "userId";

        SearchFragment.CombinedSearchResponse combinedSearchResponse =
                new SearchFragment.CombinedSearchResponse();
        combinedSearchResponse.tagSearchAnchor = tag;
        combinedSearchResponse.userSearchAnchor = userId;
        combinedSearchResponse.list = new ArrayList<>();

        List<User> mockPostUsers = MockModelFabric.createMockListOfUsers(10);
        List<Tag> mockPostTags = MockModelFabric.createMockListOfTags(10, tag);

        combinedSearchResponse.list.addAll(mockPostTags);
        combinedSearchResponse.list.addAll(mockPostUsers);

        Collections.sort(combinedSearchResponse.list, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                if (lhs instanceof Tag) {
                    Tag tag = (Tag) lhs;
                    if (rhs instanceof Tag) {
                        Tag tagTwo = (Tag) rhs;
                        return (int) (tag.postCount - tagTwo.postCount);
                    } else if (rhs instanceof User) {
                        User user = (User) rhs;
                        return (int) (tag.postCount - user.followerCount);
                    }
                } else if (lhs instanceof User) {
                    User user = (User) lhs;
                    if (rhs instanceof Tag) {
                        Tag tagTwo = (Tag) rhs;
                        return (int) (user.followerCount - tagTwo.postCount);
                    } else if (rhs instanceof User) {
                        User userTwo = (User) rhs;
                        return user.followerCount - userTwo.followerCount;
                    }
                }
                return 0;
            }
        });

        VineyardService.UserResponse usersResponse = new VineyardService.UserResponse();
        usersResponse.data = new VineyardService.UserResponse.Data();
        usersResponse.data.anchorStr = userId;
        usersResponse.data.records = mockPostUsers;
        when(mMockVineyardService.searchByUser(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(usersResponse));

        VineyardService.TagResponse tagsResponse = new VineyardService.TagResponse();
        tagsResponse.data = new VineyardService.TagResponse.Data();
        tagsResponse.data.anchorStr = tag;
        tagsResponse.data.records = mockPostTags;
        when(mMockVineyardService.searchByTag(anyString(), anyInt(), anyString()))
                .thenReturn(Observable.just(tagsResponse));

        TestSubscriber<SearchFragment.CombinedSearchResponse> result = new TestSubscriber<>();
        mDataManager.search(userId, 0, tag, 0, userId).subscribe(result);
        result.assertNoErrors();
        result.assertValue(combinedSearchResponse);
    }

}