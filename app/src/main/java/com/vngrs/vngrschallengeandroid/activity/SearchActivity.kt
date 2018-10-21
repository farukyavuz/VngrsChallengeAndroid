package com.vngrs.vngrschallengeandroid.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.vngrs.vngrschallengeandroid.R
import com.vngrs.vngrschallengeandroid.adapter.SearchListAdapter
import com.vngrs.vngrschallengeandroid.common.*
import com.vngrs.vngrschallengeandroid.common.PreferenceHelper.defaultPrefs
import com.vngrs.vngrschallengeandroid.common.PreferenceHelper.get
import com.vngrs.vngrschallengeandroid.common.PreferenceHelper.set
import com.vngrs.vngrschallengeandroid.model.SearchResponseModel
import com.vngrs.vngrschallengeandroid.model.Tweet
import com.vngrs.vngrschallengeandroid.network.ApiService
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var job: Job? = null

    private val tweetItemClicked: (tweet: Tweet) -> Unit = {
        DetailActivity.start(this@SearchActivity, it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val prefs = defaultPrefs(this)
        val accessToken: String? = prefs[PREF_TWITTER_ACCESS_TOKEN, ""]

        if (!Util.isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_LONG).show()
            return
        }

        if (accessToken.isNullOrEmpty()) {
            getToken()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)

        val searchManager = this@SearchActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        var searchView: SearchView? = null
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this@SearchActivity.componentName))
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 2) {
                    job?.cancel()
                    job = GlobalScope.launch(Dispatchers.Default, CoroutineStart.DEFAULT, null, {
                        delay(700) //0.7 Second
                        getData(newText)
                    })
                } else if (newText.isEmpty()) {
                    resultNotFoundView.visibility = View.GONE
                }
                return false
            }

            override fun onQueryTextSubmit(query: String) = false

        })

        return super.onCreateOptionsMenu(menu)

    }

    private fun getData(queryText: String) {

        if (!Util.isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_LONG).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            resultNotFoundView.visibility = View.GONE
            searchOverlayProgressBar.show()
        }

        val prefs = defaultPrefs(this)
        val accessToken: String? = prefs[PREF_TWITTER_ACCESS_TOKEN, ""]

        val searchCall = ApiService.instance.getTweetList("Bearer $accessToken", queryText)
        searchCall.enqueue(object : Callback<SearchResponseModel> {
            override fun onFailure(call: Call<SearchResponseModel>, t: Throwable) {
                GlobalScope.launch(Dispatchers.Main) {
                    searchOverlayProgressBar.stop()
                }
                Toast.makeText(this@SearchActivity, getString(R.string.handle_twitter_token_error), Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<SearchResponseModel>, response: Response<SearchResponseModel>) {
                searchRecyclerView.adapter =
                        SearchListAdapter(this@SearchActivity, response.body()?.statuses, tweetItemClicked)
                searchRecyclerView.addItemDecoration(DividerItemDecoration(this@SearchActivity, LinearLayout.VERTICAL))
                GlobalScope.launch(Dispatchers.Main) {
                    searchOverlayProgressBar.stop()
                    if (response.body()?.statuses?.count() ?: 0 < 1) {
                        resultNotFoundView.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun getToken() {

        val service = ApiService.instance

        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getToken("Basic " + Util.getBase64String(BEARER_TOKEN_CREDENTIALS), GRANT_TYPE)
            val response = request.await()

            if (response?.accessToken != null) {
                val prefs = defaultPrefs(this@SearchActivity)
                prefs[PREF_TWITTER_TOKEN_TYPE] = response.tokenType
                prefs[PREF_TWITTER_ACCESS_TOKEN] = response.accessToken
            } else {
                Toast.makeText(this@SearchActivity, getString(R.string.handle_twitter_token_error), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}