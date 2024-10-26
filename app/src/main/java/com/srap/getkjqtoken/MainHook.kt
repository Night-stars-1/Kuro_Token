package com.srap.getkjqtoken

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Proxy


class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 过滤不必要的应用
        if (lpparam.packageName != "com.kurogame.kjq") return
        val classLoader = lpparam.classLoader
        try {
            classLoader.loadClass("com.kurogame.kjq.profile.ui.activity.AccountInfoActivity") // 用于抛出异常，判断是否加固
            hook(lpparam)
        } catch (e: ClassNotFoundException) {
            XposedHelpers.findAndHookMethod(
                "com.sagittarius.v6.StubApplication",
                classLoader,
                "onCreate",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        hook(lpparam)
                    }
                })
        }
    }

    private fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val classLoader = lpparam.classLoader

        XposedHelpers.findAndHookMethod("com.kurogame.kjq.profile.ui.fragment.ProfileFragment",
            classLoader,
            "k",
            ArrayList::class.java,
            "com.kurogame.basiclib.entity.UserCenterEntity",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val arrayList = param.args[0] as ArrayList<Any>
                    val profileServiceDataClass = classLoader.loadClass("com.kurogame.librarycommon.entity.ProfileServiceData")
                    val onItemClickClass = classLoader.loadClass("com.kurogame.librarycommon.entity.ProfileServiceData\$OnItemClick")

                    val onItemClickInstance = Proxy.newProxyInstance(
                        classLoader,
                        arrayOf(onItemClickClass)
                    ) { _, method, _ ->
                        if (method.name == "onClick") {
                            val userCenter =
                                classLoader.loadClass("com.kurogame.librarycommon.usercenter.UserCenter")

                            val userCenterInstance = XposedHelpers.callStaticMethod(userCenter, "getInstance")
                            val token = XposedHelpers.callMethod(userCenterInstance, "getToken") as String
                            if (token.isNotEmpty()) {
                                val context = XposedHelpers.callMethod(userCenterInstance, "getContext") as Context
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Token", token)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "成功复制Token到剪贴板", Toast.LENGTH_LONG).show()
                            }
                        }
                        null
                    }

                    val profileServiceDataInstance = XposedHelpers.newInstance(
                        profileServiceDataClass,
                        2131231175,
                        "获取Token",
                        onItemClickInstance
                    )
                    arrayList.add(profileServiceDataInstance)
                    super.beforeHookedMethod(param)
                }

                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                }
            })

    }
}
