package com.becker.calorie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CalorieListFragment"
private const val SAVED_SUBTITLE_VISIBLE = "subtitle"

class CalorieListFragment : Fragment() {

    private lateinit var calorieRecyclerView: RecyclerView
    private var adapter: CalorieAdapter = CalorieAdapter(emptyList())
    private val calorieListViewModel: CalorieListViewModel by lazy {
        ViewModelProviders.of(this).get(CalorieListViewModel::class.java)
    }
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeSelected(calorieId: UUID)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calorie_list, container, false)

        calorieRecyclerView =
                view.findViewById(R.id.calorie_recycler_view) as RecyclerView
        calorieRecyclerView.layoutManager = LinearLayoutManager(context)
        calorieRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        calorieListViewModel.calorieListLiveData.observe(
                viewLifecycleOwner,
                Observer { calories ->
                    calories?.let {
                        Log.i(TAG, "Got calorieLiveData ${calories.size}")
                        updateUI(calories)
                    }
                }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_calorie_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_calorie -> {
                val calorie = Calorie()
                calorieListViewModel.addCalorie(calorie)
                callbacks?.onCrimeSelected(calorie.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(calorie: List<Calorie>) {
        adapter?.let {
            it.calories = calories
        } ?: run {
            adapter = CalorieAdapter(calories)
        }
        calorieRecyclerView.adapter = adapter
    }

    private inner class CalorieHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var calorie: Calorie

        private val titleTextView: TextView = itemView.findViewById(R.id.calorie_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.calorie_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.calorie_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(calorie: Calorie) {
            this.calorie = calorie
            titleTextView.text = this.calorie.title
            dateTextView.text = this.calorie.date.toString()
            solvedImageView.visibility = if (calorie.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            callbacks?.onCrimeSelected(calorie.id)
        }
    }

    private inner class CalorieAdapter(var crimes: List<Calorie>)
        : RecyclerView.Adapter<CalorieHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CalorieHolder {
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.list_item_calorie, parent, false)
            return CalorieHolder(view)
        }

        override fun onBindViewHolder(holder: CalorieHolder, position: Int) {
            val calorie = calorie[position]
            holder.bind(calorie)
        }

        override fun getItemCount() = calorie.size
    }

    companion object {
        fun newInstance(): CalorieListFragment {
            return CalorieListFragment()
        }
    }
}