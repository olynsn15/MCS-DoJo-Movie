package com.example.projectmcsdojo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectmcsdojo.models.Film
import com.example.projectmcsdojo.util.DB

class FilmDetailPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_film_detail_page)

        val filmId = intent.getStringExtra("film_id")
        val film: Film? = filmId?.let { DB.getFilmById(this, it) }

        if (film == null) {
            finish()
            return
        }

        val tvTitleDetail = findViewById<TextView>(R.id.tvTitleDetail)
        val tvPriceDetail = findViewById<TextView>(R.id.tvPriceDetail)
        val ivPosterDetail = findViewById<ImageView>(R.id.ivPosterDetail)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val tvTotalPriceValue = findViewById<TextView>(R.id.tvTotalPriceValue)
        val btnBuy = findViewById<Button>(R.id.btnBuy)
        val ivBackButton = findViewById<ImageView>(R.id.ivBackButton)

        tvTitleDetail.text = film.film_title
        tvPriceDetail.text = "Rp. ${film.film_price}"

        Glide.with(this)
            .load(film.film_image)
            .into(ivPosterDetail)

        etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val quantity = s.toString().toIntOrNull() ?: 0
                val totalPrice = quantity * film.film_price
                tvTotalPriceValue.text = "Rp. $totalPrice"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnBuy.setOnClickListener {
            val quantity = etQuantity.text.toString()

            if (quantity.isEmpty()) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantityInt = quantity.toIntOrNull() ?: 0

            if (quantityInt <= 0) {
                Toast.makeText(this, "Please enter a value greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = DB.LOGGED_IN_USER
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filmId = intent.getStringExtra("film_id") ?: return@setOnClickListener

            DB.insertTransaction(this, currentUser.user_id, filmId, quantityInt)

            Toast.makeText(this, "Purchase successful!", Toast.LENGTH_SHORT).show()
            finish()
        }

        ivBackButton.setOnClickListener {
            finish()
        }
    }
}
