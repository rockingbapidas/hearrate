package com.bapidas.heartrate.ui.fragment.presenter

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import com.bapidas.heartrate.BR
import com.bapidas.heartrate.R
import com.bapidas.heartrate.core.HeartSupport
import com.bapidas.heartrate.core.PulseListener
import com.bapidas.heartrate.core.TimerListener
import com.bapidas.heartrate.data.DataManager
import com.bapidas.heartrate.data.model.HistoryModel
import com.bapidas.heartrate.utils.ToolsUtils

/**
 * Created by bapidas on 08/11/17.
 */
class HeartFragmentPresenter(
    private val mContext: FragmentActivity?,
    private val mHeartSupport: HeartSupport,
    private val mDataManager: DataManager
) : BaseObservable() {
    private var alertDialog: AlertDialog? = null
    private var isStarted = false
    private val timeLimit: Long = 20000
    private val interval: Long = 500

    //android data binding fields
    @get:Bindable
    var beatsPerMinute = "000"
        private set(beatsPerMinute) {
            field = beatsPerMinute
            notifyPropertyChanged(BR.beatsPerMinute)
        }

    @get:Bindable
    var progress = 0
        private set(progress) {
            field = progress
            notifyPropertyChanged(BR.progress)
        }

    fun onHelpClick(view: View?) {
        showHintDialog()
    }

    fun onStartClick(view: View?) {
        start()
    }

    private fun start() {
        if (isStarted) {
            isStarted = false
            mHeartSupport.stopPulseCheck()
        } else {
            initHeart()
        }
    }

    private fun showHintDialog() {
        if (alertDialog == null || alertDialog?.isShowing == false) {
            val dialog = AlertDialog.Builder(mContext)
            val mView =
                LayoutInflater.from(mContext).inflate(R.layout.hint_diaog, null)
            val btnOk = mView.findViewById<Button>(R.id.btnOk)
            btnOk.setOnClickListener { alertDialog?.dismiss() }
            dialog.setView(mView)
            alertDialog = dialog.create()
            alertDialog?.show()
        }
    }

    private fun showSuccessDialog() {
        if (alertDialog == null || alertDialog?.isShowing == false) {
            val dialog = AlertDialog.Builder(mContext)
            val mView =
                LayoutInflater.from(mContext).inflate(R.layout.result_success_dialog, null)
            val textView = mView.findViewById<TextView>(R.id.txtheart)
            textView.text = beatsPerMinute
            val btnCancel =
                mView.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener {
                alertDialog?.dismiss()
                beatsPerMinute = ("000")
                progress = 0
            }
            val btnSave =
                mView.findViewById<Button>(R.id.btn_save)
            btnSave.setOnClickListener { saveHeartRate() }
            dialog.setView(mView)
            alertDialog = dialog.create()
            alertDialog?.show()
        }
    }

    private fun showErrorDialog() {
        if (alertDialog == null || alertDialog?.isShowing == false) {
            val dialog = AlertDialog.Builder(mContext)
            val mView =
                LayoutInflater.from(mContext).inflate(R.layout.result_error_dialog, null)
            val btnCancel =
                mView.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener {
                alertDialog?.dismiss()
                beatsPerMinute = ("000")
                progress = 0
            }
            val btn_try =
                mView.findViewById<Button>(R.id.btn_try)
            btn_try.setOnClickListener {
                alertDialog?.dismiss()
                start()
            }
            dialog.setView(mView)
            alertDialog = dialog.create()
            alertDialog?.show()
        }
    }

    private fun saveHeartRate() {
        val timeStamp = System.currentTimeMillis()
        val date = ToolsUtils.getDate(timeStamp)
        val time = ToolsUtils.getTime(timeStamp)
        if (mDataManager.insertHistory(HistoryModel(beatsPerMinute, date, time))) {
            Toast.makeText(mContext, "Heart rate saved", Toast.LENGTH_SHORT).show()
            alertDialog?.dismiss()
            beatsPerMinute = ("000")
            progress = 0
        } else {
            Toast.makeText(mContext, "Heart rate not saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initHeart() {
        mHeartSupport.addOnResultListener(object : PulseListener {
            override fun onPulseResult(pulse: String) {
                beatsPerMinute = if (pulse.length == 2) {
                    ("0$pulse")
                } else {
                    (pulse)
                }
            }
        }).addOnTimerListener(object : TimerListener {
            override fun onTimerStarted() {
                isStarted = true
                beatsPerMinute = ("000")
                progress = 0
            }

            override fun onTimerRunning(milliSecond: Long) {
                val n = timeLimit - milliSecond
                progress = (n * 100 / timeLimit).toInt()
            }

            override fun onTimerStopped() {
                progress = 100
                Handler().postDelayed({
                    isStarted = false
                    val value = beatsPerMinute.toInt()
                    if (value == 0 || value < 50) {
                        showErrorDialog()
                    } else {
                        showSuccessDialog()
                    }
                }, 800)
            }
        }).startPulseCheck(timeLimit)
    }

    companion object {
        private val TAG = HeartFragmentPresenter::class.java.simpleName

        @JvmStatic
        @BindingAdapter("updateProgress")
        fun setProgressAnimation(mProgressBar: ProgressBar?, progress: Int) {
            val animation = ObjectAnimator.ofInt(mProgressBar, "progress", progress)
            animation.duration = 500
            animation.interpolator = LinearInterpolator()
            animation.start()
        }
    }

}