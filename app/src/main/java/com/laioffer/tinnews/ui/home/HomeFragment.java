package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements CardStackListener {
    private HomeViewModel viewModel;
    //viewbinding create this, this is implicit by layout fragment_home in layout
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }

    private void backCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.rewind();
//        Article article = articles.get(layoutManager.getTopPosition() - 1);
//        viewModel.setFavoriteArticleInput();
    }

    private void refresh() {

        // Setup CardStackView
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        // this is a cardStackListener
        layoutManager.setStackFrom(StackFrom.TopAndRight);
        layoutManager.setVisibleCount(5);
        binding.homeCardStackView.setLayoutManager(layoutManager);

        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);
        viewModel.setCountryInput("us");
//        viewModel.
        viewModel.getTopHeadlines().observe(getViewLifecycleOwner(),
                new Observer<NewsResponse>() {
                    @Override
                    public void onChanged(NewsResponse newsResponse) {
                        if(newsResponse != null) {
//                            Log.d("HomeFragment", newsResponse.toString());
                            articles = newsResponse.articles;
                        }
                    }
                });


        // pagnation -> retrofit ; how

//        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
//                .setDirection(Direction.Bottom)
//                .setDuration(Duration.Normal.duration)
//                .build();
//        layoutManager.setSwipeAnimationSetting(setting);
//        while(layoutManager.getTopPosition() > 1) {
//            binding.homeCardStackView.rewind();
//        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup CardStackView
        CardSwipeAdapter swipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        // this is a cardStackListener
        layoutManager.setStackFrom(StackFrom.TopAndRight);
        layoutManager.setVisibleCount(5);
        binding.homeCardStackView.setLayoutManager(layoutManager);
//        binding.homeCardStackView.scrollToPosition();  back to 0;
        binding.homeCardStackView.setAdapter(swipeAdapter);

        // Handle like unlike button clicks
        binding.homeLikeButton.setOnClickListener(v -> swipeCard(Direction.Right));
        binding.homeUnlikeButton.setOnClickListener(v -> swipeCard(Direction.Left));
        // rewind
        binding.homeRewindButton.setOnClickListener(v -> backCard(Direction.Bottom));
        // refresh the stack
        binding.homeRefreshButton.setOnClickListener(v -> refresh());



        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);
        //use factory to retain state! description in Pri..
        viewModel.setCountryInput("us");
        viewModel.getTopHeadlines().observe(getViewLifecycleOwner(),
//                        newsResponse -> {
//                            if (newsResponse != null) {
//                                Log.d("HomeFragment", newsResponse.toString());
//                            }
//                        });
                new Observer<NewsResponse>() {
                    @Override
                    public void onChanged(NewsResponse newsResponse) {
                        if(newsResponse != null) {
                            Log.d("HomeFragment", newsResponse.toString());
                            articles = newsResponse.articles;
                            swipeAdapter.setArticles(articles);
                        }
                    }
                });
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());

        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked "  + layoutManager.getTopPosition());
            // first swipe, then add. so - 1
            Article article = articles.get(layoutManager.getTopPosition() - 1);
            viewModel.setFavoriteArticleInput(article);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}