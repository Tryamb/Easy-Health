package com.tryamb.healthcare.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tryamb.healthcare.EmotionRecognition
import com.tryamb.healthcare.NearbyDoctors
import com.tryamb.healthcare.SkinScanActivity
import com.tryamb.healthcare.databinding.FragmentHomeBinding

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        binding.help.setOnClickListener {
//            startActivity(Intent(requireContext(), ChatScreen::class.java))
//        }
//        binding.skin.setOnClickListener{
//            startActivity(Intent(requireContext(), SkinDisease::class.java))
//        }
//        binding.mentalHealth.setOnClickListener{
//            startActivity(
//                Intent(
//                    requireContext(),
//                    MentalHealthPage::class.java
//                )
//            )
//        }
        binding.skinScan.setOnClickListener{
            startActivity(Intent(requireContext(), SkinScanActivity::class.java))
        }
//        binding.about.setOnClickListener {
//            val openURL = Intent(Intent.ACTION_VIEW)
//            openURL.data = Uri.parse("https://skin-disease-test.netlify.app/faq.html")
//            startActivity(openURL)
//        }
        binding.testStress.setOnClickListener{
            startActivity(Intent(requireContext(), EmotionRecognition::class.java))
        }

        binding.consultDoctor.setOnClickListener {
            startActivity(Intent(requireContext(),NearbyDoctors::class.java))
        }

        return binding.root
    }

}