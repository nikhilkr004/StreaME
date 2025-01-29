package com.example.visionary

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.visionary.Fragments.HomeFragment
import com.example.visionary.Fragments.ProfileFragment
import com.example.visionary.Fragments.SearchFragment
import com.example.visionary.databinding.ActivityMainBinding
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
         val HOME_ITEM = R.id.homeFragment
         val SEARCH_ITEM=R.id.searchFragment
         val PROFILE_ITEM=R.id.profileFragment
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setStatusbarColor()


        val bottomNavigation=findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)

        bottomNavigation.add(
            CurvedBottomNavigation.Model(1,"",R.drawable.home)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(2,"",R.drawable.searchbtn)
        )
        bottomNavigation.add(
            CurvedBottomNavigation.Model(3,"",R.drawable.userimg)
        )


        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1 ->{
                    replaceFragment(HomeFragment())
                }
                2 ->{
                    replaceFragment(SearchFragment())

                }
                3->{
                    replaceFragment(ProfileFragment())

                }

            }
        }

        ///dafault bottom tap select
        replaceFragment(HomeFragment())
        bottomNavigation.show(1)
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment,fragment)
            .commit()
    }


    private fun setStatusbarColor(){
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.black4, theme)
        }
    }
}