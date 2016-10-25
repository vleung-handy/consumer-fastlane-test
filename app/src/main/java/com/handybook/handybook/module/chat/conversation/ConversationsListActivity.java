package com.handybook.handybook.module.chat.conversation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.handybook.handybook.core.BaseApplication.getContext;

public class ConversationsListActivity extends AppCompatActivity
{

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);
        ButterKnife.bind(this);


        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        manager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(manager);


    }
}
