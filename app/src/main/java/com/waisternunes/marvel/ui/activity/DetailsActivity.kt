package com.waisternunes.marvel.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.waisternunes.marvel.R
import com.waisternunes.marvel.api.ApiMovie
import com.waisternunes.marvel.utils.PARAM_MOVIE
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var apiMovie: ApiMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
    }

    private fun initViews() {
        apiMovie = intent.getSerializableExtra(PARAM_MOVIE) as ApiMovie

        tv_title.text = apiMovie.title
        tv_genre.text = apiMovie.genre
        tv_plot.text = apiMovie.plot

        tv_released.text = getString(R.string.released, apiMovie.released)
        tv_runtime.text = getString(R.string.runtime, apiMovie.runtime)
        tv_director.text = getString(R.string.director, apiMovie.director)
        tv_actors.text = getString(R.string.actors, apiMovie.actors)
        tv_writer.text = getString(R.string.writer, apiMovie.writer)

        if (apiMovie.poster.isNotEmpty())
            Picasso.get().load(apiMovie.poster).into(iv_poster)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}