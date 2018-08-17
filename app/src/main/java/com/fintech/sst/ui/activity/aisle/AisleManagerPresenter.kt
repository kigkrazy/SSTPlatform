package com.fintech.sst.ui.activity.aisle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import com.fintech.sst.helper.PermissionUtil
import com.fintech.sst.net.Configuration
import com.fintech.sst.net.Constants
import com.fintech.sst.net.ProgressObserver
import com.fintech.sst.net.ResultEntity
import com.fintech.sst.net.bean.UserInfoDetail
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AisleManagerPresenter(val view: AisleManagerContract.View, private val model: AisleManagerModel = AisleManagerModel()) : AisleManagerContract.Presenter, LifecycleObserver {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var clickNum = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        if (Configuration.noLogin()) {
            view.toLogin()
            return
        } else {
            Constants.baseUrl = Configuration.getUserInfoByKey(Constants.KEY_ADDRESS)
        }
        userInfo()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (!PermissionUtil.isNotificationListenerEnabled()) {
            view.toNotifactionSetting()
            view.showToast("请打开随身听通知监听权限")
        }
        clickNum = 0
    }

    override fun userInfo() {
        model.userInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ProgressObserver<ResultEntity<UserInfoDetail>, AisleManagerContract.View>(view) {
                    override fun onNext_(t: ResultEntity<UserInfoDetail>?) {
                        view.updateUserInfo(t?.result)
                    }

                    override fun onError(error: String) {
                        view.showToast(error)
                    }
                })
    }

    override fun toOrder() {
        view.toOrderList()
    }

    override fun toNoticeList() {
        if (++clickNum >= 7) {
            view.toNoticeList()
            return
        } else {
            compositeDisposable.clear()
            if (clickNum > 2)
                view.showToast("再点${7 - clickNum}次进入通知详情页")
        }
        val d = Single.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe { _ -> clickNum = 0 }
        compositeDisposable.add(d)
    }
}