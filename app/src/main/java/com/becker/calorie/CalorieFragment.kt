package com.becker.calorie


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.Date
import java.util.UUID

private const val TAG = "CalorieFragment"
private const val ARG_CALORIE_ID = "calorie_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class CalorieFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var calorie: Calorie
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val calorieDetailViewModel: CalorieDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CalorieDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calorie = Calorie()
        val calorieId: UUID = arguments?.getSerializable(ARG_CALORIE_ID) as UUID
        calorieDetailViewModel.loadCalorie(calorieId)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calorie, container, false)

        titleField = view.findViewById(R.id.calorie_title) as EditText
        dateButton = view.findViewById(R.id.calorie_date) as Button
        solvedCheckBox = view.findViewById(R.id.calorie_solved) as CheckBox

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calorieId = arguments?.getSerializable(ARG_CALORIE_ID) as UUID
        calorieDetailViewModel.loadCalorie(calorieId)
        calorieDetailViewModel.calorieLiveData.observe(
                viewLifecycleOwner,
                Observer { calorie ->
                    calorie?.let {
                        this.calorie = calorie
                        updateUI()
                    }
                })

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.supportActionBar?.setTitle(R.string.new_calorie)
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
                calorie.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                calorie.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(calorie.date).apply {
                setTargetFragment(this@CalorieFragment, REQUEST_DATE)
                show(this@CalorieFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        calorieDetailViewModel.saveCalorie(calorie)
    }

    override fun onDateSelected(date: Date) {
        calorie.date = date
        updateUI()
    }

    private fun updateUI() {
        titleField.setText(calorie.title)
        dateButton.text = calorie.date.toString()
        solvedCheckBox.isChecked = calorie.isSolved
    }

    companion object {

        fun newInstance(calorieId: UUID): CalorieFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CALORIE_ID, calorieId)
            }
            return CalorieFragment().apply {
                arguments = args
            }
        }
    }
}