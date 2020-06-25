package com.waisternunes.marvel.ui.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import com.waisternunes.marvel.R
import com.waisternunes.marvel.api.ApiMovie
import com.waisternunes.marvel.ui.adapter.MoviesAdapter
import com.waisternunes.marvel.utils.HAWK_MOVIES
import com.waisternunes.marvel.utils.ItemMoveCallback
import com.waisternunes.marvel.utils.StartDragListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Type

class MainActivity : AppCompatActivity(), StartDragListener {

    private var moviesList: ArrayList<ApiMovie> = ArrayList()
    private var moviesAdapter: MoviesAdapter? = null
    private var touchHelper: ItemTouchHelper? = null
    private var searchTerms = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(this)
        rv_movies.layoutManager = layoutManager

        moviesAdapter = MoviesAdapter(this, this)

        val callback: ItemTouchHelper.Callback = ItemMoveCallback(moviesAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper!!.attachToRecyclerView(rv_movies)

        rv_movies.adapter = moviesAdapter

        val hawkApiMovies: ArrayList<ApiMovie> = Hawk.get(HAWK_MOVIES, ArrayList())

        if (hawkApiMovies.isNotEmpty()) {
            moviesList = hawkApiMovies

            populateRecycler()
        }

        getMoviesFromApi()
    }

    private fun populateRecycler() {
        val moviesToShow: ArrayList<ApiMovie> = ArrayList()

        if (searchTerms.isNotEmpty()) {
            moviesList.forEach { movie ->
                if (movie.title.contains(searchTerms, true))
                    moviesToShow.add(movie)
            }
        } else {
            moviesToShow.addAll(moviesList)
        }

        if (moviesToShow.isNotEmpty()) {
            moviesAdapter?.setData(moviesToShow)
        } else {
            tv_error_message.visibility = View.VISIBLE
            bt_try_again.visibility = View.VISIBLE
            bt_try_again.setOnClickListener {
                getMoviesFromApi()
            }
        }
    }

    private fun getMoviesFromApi() {
        if (moviesList.isEmpty()) {
            tv_error_message.visibility = View.GONE
            bt_try_again.visibility = View.GONE
            pb_downloading.visibility = View.VISIBLE
        } else {
            pb_updating.visibility = View.VISIBLE
        }

        val apiRoute = "https://private-b34167-rvmarvel.apiary-mock.com/saga"

        apiRoute.httpGet().responseString { _, _, result ->
            if (pb_updating == null || isFinishing) return@responseString

            pb_updating.visibility = View.GONE
            pb_downloading.visibility = View.GONE

            val (data, error) = result

            if (error == null) {
                val listType: Type = object : TypeToken<ArrayList<ApiMovie?>?>() {}.type
                val apiMovies: ArrayList<ApiMovie>? = Gson().fromJson(data, listType)

                if (apiMovies != null) {
                    if (moviesList.isEmpty()) {

                        moviesList.addAll(apiMovies)

                    } else {

                        moviesList.forEachIndexed { index, movie ->
                            if (apiMovies.contains(movie))
                                moviesList[index] = movie
                            else
                                moviesList.removeAt(index)
                        }

                        apiMovies.forEach { movie ->
                            if (!moviesList.contains(movie))
                                moviesList.add(movie)
                        }
                    }

                    Hawk.put(HAWK_MOVIES, moviesList)
                }
            }

            populateRecycler()
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper?.startDrag(viewHolder)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        val actionSearch = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = actionSearch?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(terms: String): Boolean {
                filterMovies(terms)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(terms: String): Boolean = filterMovies(terms)
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun filterMovies(terms: String = ""): Boolean {
        searchTerms = terms

        populateRecycler()

        return true
    }

}