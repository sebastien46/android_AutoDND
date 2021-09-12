package moe.dic1911.autodnd.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.dic1911.autodnd.R
import moe.dic1911.autodnd.data.AppEntry
import moe.dic1911.autodnd.data.Storage
import moe.dic1911.autodnd.databinding.FragmentMainBinding
import moe.dic1911.autodnd.databinding.FragmentMainBinding.inflate
import java.lang.StringBuilder

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel
    private lateinit var pkgList: ArrayList<AppEntry>

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    fun reload() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
            setAdapter(AppListAdapter())
            passContext(requireActivity().applicationContext)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        val recyclerView: RecyclerView = root.findViewById(R.id.app_list_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = AppListAdapter()
        Log.d("030_lst", "${pageViewModel.getIndex()}, size = ${Storage.getAppList(pageViewModel.getIndex()!!)!!.size}")
        adapter.appList = Storage.getAppList(pageViewModel.getIndex()!!)!!
        recyclerView.adapter = adapter
        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
            Log.d("030.txt", it.toString())
            textView.text = it
        })
        pageViewModel.applist.observe(viewLifecycleOwner, Observer<ArrayList<AppEntry>> {
            Log.d("030-list", it.size.toString())
            (recyclerView.adapter as AppListAdapter?)?.appList = it
            (recyclerView.adapter as AppListAdapter?)?.notifyDataSetChanged()
        })
        if (Storage.prefs_str.hasObservers()) {
            Storage.prefs_str.removeObservers(this)
        }
        Storage.prefs_str.observe(viewLifecycleOwner, Observer<ArrayList<String>> {
            if (pageViewModel.getIndex() == 0) {
                Log.d("030-list", it.size.toString())
                (recyclerView.adapter as AppListAdapter?)?.appList = Storage.getAppList(0)!!
                (recyclerView.adapter as AppListAdapter?)?.notifyDataSetChanged()
            }
        })

        _binding = inflate(inflater, container, false)

        // val view = binding.root
        return root
    }

    fun updateList() {
        pageViewModel.updateAdapter(Storage.getAppList(pageViewModel.getIndex()!!)!!)
    }

    override fun onResume() {
        super.onResume()
        val sb = StringBuilder()
        val txt = binding.warning
        when (Storage.setupStatus) {
            0 -> txt.visibility = View.GONE
            1 -> {
                txt.visibility = View.VISIBLE
                sb.append(getString(R.string.setup_tip)).append("\n")
                sb.append(getString(R.string.notification_policy_tip))
            }
            2 -> {
                txt.visibility = View.VISIBLE
                sb.append(getString(R.string.setup_tip)).append("\n")
                sb.append(getString(R.string.accessbility_svc_tip))
            }
            3 -> {
                txt.visibility = View.VISIBLE
                sb.append(getString(R.string.setup_tip)).append("\n")
                sb.append(getString(R.string.notification_policy_tip)).append("\n")
                sb.append(getString(R.string.accessbility_svc_tip))
            }
        }
        txt.text = sb.toString()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int, mContext: Context): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}