package com.vngrs.vngrschallengeandroid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.squareup.picasso.Picasso
import com.vngrs.vngrschallengeandroid.R
import com.vngrs.vngrschallengeandroid.model.Tweet
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var tweet: Tweet

    companion object {
        fun start(context: Context, tweet: Tweet) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("tweet", tweet)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //TODO need null check.
        tweet = intent.getParcelableExtra("tweet")

        Picasso.get().load(tweet.user?.profileImageUrl).into(detailUserPhoto)
        detailUserName.text = tweet.user?.name ?: ""
        detailScreenName.text = "@" + tweet.user?.screenName
        detailDesc.text = tweet.text


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}