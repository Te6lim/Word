package com.te6lim.word.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.te6lim.word.MainActivity
import com.te6lim.word.R

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mainActivity = requireActivity() as MainActivity
        val helpView = inflater.inflate(R.layout.fragment_help, container, false)

        val helpToolbar = helpView.findViewById<MaterialToolbar>(R.id.helpToolBar)

        mainActivity.setSupportActionBar(helpToolbar)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (requireActivity() as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        mainActivity.supportActionBar?.title = getString(R.string.help)

        return helpView.rootView
    }
}