package com.srap.getkjqtoken

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.Base64
import androidx.core.graphics.toColorInt

val iconToken: Drawable? get() {
    val base64Str = "iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAYAAAA5ZDbSAAAAAXNSR0IArs4c6QAACNNJREFUeF7tXVXIbUUU/q6KrdjdiC0qKgZ2XEz0RQUDExNsLPRF7MLAxKtiICqigt2BASZ2d2B35/5gn8v5j3vvWVNn5sxZ6/Vf/Z3ZM7NmzfyToFR0BiYVHZ0GBwW48B+BAqwAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAF56BwsPTEawAB8/AMgD2ArAVgJUN2n8AcA2AawE8FdyTMVA4jBFMGxcD2C9QPu8GsDeATwPpK1pNTIA3A3Bf5OydAOCkyDZGWn0MgLcEcOeQs3IYgHOHbHMkzIUEeFoA3wGYNWHkcwP4JqH97EyHAphz4uWZRHcRgIMy8SW5GyEAfgnASskjmejAO9XKe+nMfEriji/AvwKY0dPz5wC8DeBjAH8CmBPAKgDW8tT7I4DZPXWMvLgPwP8A1m98fFRtb/YFwK2ODXG1fKKNQM37LYC5HOSKEXEF2BbcUwEcFyBrHN1vAJjXQtfLgoKKhbrRYnUB+BcAMwnDvAPANkJeG7aF60+6VObQ6tN/npS5JD5bgF+0GA2zAOCPISaxhLmL0MBsAH4S8hbDZgPwrnVd2BQ8F0uLmpgC/n1DAA8L9P1bzePTCPiKYpECzMT8LYj8SQDrCvhCsyworE0fU00vp4c2nrM+KcDfC7YcqcDt5Xc+AJ8Lki2NWaAqfxZJsJsAeMAQyocAFs8g3OUBvGrw40YAO2Xg61BckADMuctEEj0mHaH+fiyAUwzKcvI3VNyNekyBrgPgCYMHrBaxapQTSaaUnPyV+LIkgPcljP08JoBNBY3bAGxva3QI/NJF4RBcCWrCGuQugPk3AtxFph9I0Ogsld0KYDtLmVFgtwK5C6ALARzYEfHZVeHgyMwzIlk/ZB5Co3tikLsANiUn5Ohdod4/0ya3W6aVsBSUDwAsJmUeMT4RyK4AhzilmR7AJwDmaUnsVwBYc/7DI/Hs2mR5tVQygtwGsCkx2wK43SNre1T75iuF8rsDuFrI28Rm+hJ5qM5CtBPkNoDPqrY+R3S47/N53gEAiw02xMKErUxP/0M2hjLj3UjoTyvIbUDxDHXFCABPV3dtCP2ewEZZST3cRXeuMltUnS13CZ1rBLkN4K7Pms+pzBeWh/X9sVF2fmGwJbF5gewC8KMAeETnQr7zoc/U4OJvLjLOILsAfBWAPR0iXwPA0w5y/SKrA2CT3jiSE8guAJ8B4GiHDO9f31FyEJ0qwvtNl/koGHFZa5BdAD6zasU5yiFRIQA+AMAlDrZLErEC2QVg7km5N7Wltesqla1cPz9Pt8b9GumOAG4QJvE1F4AfB7Ce0MAgmy6yHBNXi7HliC3IEuLNzskuAFO562r2y47SpMlpyrItZ5SIC0tW7EwX3SUxUQ8rgBKaej+rDShTe6wrwKw//y7xsIGHsrzaMip0P4BN+5xlL/lvjs4P6upSM2EhmqJUuTOA6ywDpcz1ljI9dtdSJV8QkPZc97tGINv6wV1Krq9UZVqetklo82r65I9hKrUBzNuCvDXYRlzJ3SOx2MJjU4/mouImD1s+8z7vO/O6jJR4Ef0cA/MzANYUKmSXqHRaYsPh64N6XY8LQ8yHvDDOUdIWAEuTC3nWnyU9ZZJcS+rgPN7kBXQJ8UfHKeevDuafAcwsUVZfsOMR7v/IFWAqcp2Hm/yYXH2Cl63/wMtl9woDM7Hxtr/NCOzSR//ebGBYtWoNet7kSMvft2557oKHKtJbGJ1rky6QeFnr4A7Hx7Flh8WaS/tywpMeTlc+NFjbt5lSjIPMxGAyZpL3CdxXlolb31dJgzwvAfDGJBdSoeJncyMv6/FCvYTY5TKDhNHkoKltlq/p8DOTG/lsx3KLZdCfr21qCSaAJYuUHK9lshE/5Ws/PVC45+f8zDJtCOKWyeo9FBPAdMr0mSaPRE+IACU6Tha8JtDl7+EAuL7wpSnVJfV9aiWS+10me3z2gm+QWZEEGK5wTXtevr2RQ3sqH295wZCBxwBsYOBZojpQec8qkxOZl2qQZ645x4rmzgHbFxgWvK2uSgCmMB8F5ae4i3jKw096KuKemW24JpLGTD0sjXIPLCXu69nq20V8T4zviknJ6wxcGiyLEl2b8p6zqe4I8+qq5GIWC/Z86daG2EGymkDgkKrgcL6AjyxskHtXwMv3Pk1XdzvVSAGmErbpXCFwiqNoEQFfKBab81GbePv9YwsxW4nbiAs6Vp5sibnil6eJlqtfFLLVOYHfNmDTKVO/8mFcK7Xxh08vsZzoSk3zcogvFotJgy8AsfrGOrg32QJMgzbPKMXaJ3OR9IhF9FxZH2/B38Xam5d5FPhgIJ0scvReAAp6LOoCMGMyFUAG4+YJS9dNCWmeXDozOYeyG3MsyRVgF5Apw5v3XEHebJFtnq+y/rubhUyPlcdtCzjIFSPiA7Dt57otaaz0vFW/XMeVOucfbresKjYNyj/rWMAUA6ApEF+AqZ8A8cgsJwqx+MkpHmdfQgBM42xt4bOCOZDNfjQHf6P6EApgOskDap50zBHV43blrJnTNqtuSnUGQgLcS+rGAbcPUqD4NtZpUuZx4osBcC9/XCixST6mjZD72yJxj5n8/oSZ2n9skvts/TYXX7VVMmRgWAD3u8GXA/hfUdjDa/rHGZzTb6l7okNVjcbqR5EC4LFKcOpgFeDUCES2rwBHTnBq9QpwagQi21eAIyc4tXoFODUCke0rwJETnFq9Apwagcj2FeDICU6tXgFOjUBk+wpw5ASnVq8Ap0Ygsn0FOHKCU6tXgFMjENm+Ahw5wanVK8CpEYhsXwGOnODU6hXg1AhEtq8AR05wavUKcGoEIttXgCMnOLV6BTg1ApHtK8CRE5xavQKcGoHI9hXgyAlOrV4BTo1AZPsKcOQEp1avAKdGILJ9BThyglOrV4BTIxDZ/n/8NC5+3A+YXwAAAABJRU5ErkJggg=="
    return Base64.getDecoder().decode(base64Str).inputStream().use { stream ->
        Drawable.createFromStream(stream, null)
    }
}

fun copyToken(classLoader: ClassLoader, context: Context) {
    val userCenter =
        classLoader.loadClass("com.kurogame.librarycommon.usercenter.UserCenter")

    val userCenterInstance = XposedHelpers.callStaticMethod(userCenter, "getInstance")
    val token = XposedHelpers.callMethod(userCenterInstance, "getToken") as String
    if (token.isNotEmpty()) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Token", token)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "成功复制Token到剪贴板", Toast.LENGTH_LONG)
            .show()
    } else {
        Toast.makeText(context, "获取Token失败", Toast.LENGTH_LONG)
            .show()
    }
}
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
        XposedHelpers.findAndHookMethod(
            "com.kurogame.librarycommon.ui.view.MHorExpandLayout",
            classLoader,
            "a",
            ArrayList::class.java,
            object : XC_MethodHook() {
                @SuppressLint("SetTextI18n")
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val frameLayout = param.thisObject as FrameLayout
                    val context = frameLayout.context
                    val linearLayout = LinearLayout(context)
                    linearLayout.orientation = LinearLayout.VERTICAL
                    linearLayout.layoutParams = FrameLayout.LayoutParams(132, 171)
                    linearLayout.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

                    val tokenIcon = ImageView(context)
                    tokenIcon.layoutParams = ViewGroup.LayoutParams(120, 120)
                    tokenIcon.setImageDrawable(iconToken)
                    tokenIcon.setColorFilter("#444444".toColorInt(), PorterDuff.Mode.SRC_IN)
                    tokenIcon.scaleType = ImageView.ScaleType.FIT_XY

                    val tokenText = TextView(context)
                    tokenText.text = "Token"
                    tokenText.textSize = 10f
                    tokenText.layoutParams = ViewGroup.LayoutParams(131, 57)
                    tokenText.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    tokenText.setPadding(0, 0, 0, 0)

                    linearLayout.setOnClickListener {
                        copyToken(classLoader, context)
                    }
                    linearLayout.addView(tokenIcon)
                    linearLayout.addView(tokenText)
                    frameLayout.addView(linearLayout)
                    super.afterHookedMethod(param)
                }
            })
    }
}
