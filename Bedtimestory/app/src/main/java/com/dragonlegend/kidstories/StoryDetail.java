package com.dragonlegend.kidstories;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dragonlegend.kidstories.Api.ApiInterface;
import com.dragonlegend.kidstories.Api.Client;
import com.dragonlegend.kidstories.Api.Responses.CommentResponse;
import com.dragonlegend.kidstories.Api.Responses.StoryResponse;
import com.dragonlegend.kidstories.Model.Story;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryDetail extends AppCompatActivity {
    public static final String STORY_ID = "story_id";
    ImageView mStoryImage;
    TextView mTitle, mDetail,mPost;
    ImageButton mBookmark;
    EditText mAddComment;
    ProgressBar mProgressBar;
    LinearLayout mLinearLayout,commentLayout;
    String comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //customize custom toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_primary);
        getSupportActionBar().setElevation(0);

        initViews();
        Client.getInstance().create(ApiInterface.class).getStory(getIntent().getStringExtra(STORY_ID))
                .enqueue(new Callback<StoryResponse>() {
                    @Override
                    public void onResponse(Call<StoryResponse> call, Response<StoryResponse> response) {
                        if(response.isSuccessful()){
                            Story story = response.body().getData().getStory();
                            Glide.with(StoryDetail.this)
                                    .load(story.getImage())
                                    .into(mStoryImage);
                            mTitle.setText(story.getTitle());
                            mDetail.setText(story.getStory());
                        }
                        mProgressBar.setVisibility(View.GONE);
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<StoryResponse> call, Throwable t) {

                    }
                });

        mAddComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    commentLayout.setBackgroundResource(R.drawable.comment_bg);
                    mPost.setVisibility(View.VISIBLE);
                }

            }
        });
    mPost.setOnClickListener(view -> {
        //post the comment
        comments  = mAddComment.getText().toString().trim();
        addComment(comments);
    });

    }

    public void initViews(){
        mStoryImage = findViewById(R.id.detail_image);
        commentLayout=findViewById(R.id.commentLayout);
        mTitle = findViewById(R.id.detail_title);
        mPost = findViewById(R.id.post);
        mDetail = findViewById(R.id.story);
        mBookmark = findViewById(R.id.bookmark_button);
        mAddComment = findViewById(R.id.add_comment);
        mProgressBar = findViewById(R.id.progress);
        mLinearLayout = findViewById(R.id.story_ll);
    }

    private void addComment(String comment){
        Client.getInstance().create(ApiInterface.class).addComment(getIntent().getStringExtra(STORY_ID), comment)
                .enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(StoryDetail.this, "Successful upload", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(StoryDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Toast.makeText(StoryDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
