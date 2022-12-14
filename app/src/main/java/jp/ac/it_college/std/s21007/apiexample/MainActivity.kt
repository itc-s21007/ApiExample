package jp.ac.it_college.std.s21007.apiexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jp.ac.it_college.std.s21007.apiexample.Json.PokemonInfo
import jp.ac.it_college.std.s21007.apiexample.Json.PokemonService
import jp.ac.it_college.std.s21007.apiexample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.picasso.Picasso

private const val BASE_URL = "https://pokeapi.co/api/v2/"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btDisplay.setOnClickListener {
            val id = if (binding.etId.text.isNotEmpty()) binding.etId.text.toString().toInt() else 1
            showPokemonInfo(id)
        }
    }

    @UiThread
    private fun showPokemonInfo(id: Int){
        lifecycleScope.launch {
            val info =getPokemonInfo(id)
            val url = info.sprites.other.officialArtwork.frontDefault
            Picasso.get().load(url).into(binding.imgPokemon)
            binding.name.text = getString(R.string.PokemonName, info.name)
            //binding.type.text = getString(R.string.Type, info.type)

        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @WorkerThread
    private suspend fun getPokemonInfo(id: Int): PokemonInfo {
        return withContext(Dispatchers.IO) {
            val retrofit = Retrofit.Builder().apply {
                baseUrl(BASE_URL)
                addConverterFactory(MoshiConverterFactory.create(moshi))
            }.build()
            val service: PokemonService = retrofit.create(PokemonService::class.java)
            service.fetchPokemon(id).execute().body() ?: throw IllegalStateException("????????????????????????????????????")
        }
    }
}
