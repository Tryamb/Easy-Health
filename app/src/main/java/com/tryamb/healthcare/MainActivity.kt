package com.tryamb.healthcare
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tryamb.healthcare.databinding.ActivityMain3Binding
import com.tryamb.healthcare.fragments.Home
import com.tryamb.healthcare.login.SignInActivity


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMain3Binding
    private val TIME_INTERVAL = 3000


    private var mBackPressed: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawer = binding!!.drawerLayout
        val mToolbar=binding!!.toolbar
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        loadFragment(Home())

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment,fragment)
        transaction.commit()
    }
    override fun onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            finishAffinity()
        }
        else {
            Toast.makeText(baseContext, "Tap back button again to exit", Toast.LENGTH_SHORT).show() }

        mBackPressed = System.currentTimeMillis();

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.dashboard){
            item.isChecked=true
        }

        when (item.itemId) {
            R.id.dashboard -> {
                loadFragment(Home())
            }

            R.id.share -> try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Skin N Sense | Healthcare")
                var shareMessage = "\nDownload the app\n\n"
                shareMessage =
                    """
                    ${shareMessage}https://drive.google.com/drive/folders/1Le4cSzomzu5s9PWejpUEywPv8jUyeqC4?usp=sharing

                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to share", Toast.LENGTH_SHORT).show()
            }
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,SignInActivity::class.java))
            }
            R.id.healthfiles ->{

            }
            R.id.about ->{
                try {
                    val intent34 = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("mailto:" + "tryamb.2024cs1073@kiet.edu")
                    )
                    intent34.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
                    intent34.putExtra(Intent.EXTRA_TEXT, "your_text")
                    startActivity(intent34)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(applicationContext, "Mailing Problem", Toast.LENGTH_LONG).show()
                }

            }


        }
        val drawer = binding!!.drawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


}
