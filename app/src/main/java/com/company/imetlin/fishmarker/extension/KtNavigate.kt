package com.company.imetlin.fishmarker.extension

import android.R
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @param container fragments container layout id
 * @param backStackTag if null, then fragment does not adds to backstack
 */
fun Fragment.navigateTo(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    byFade: Boolean = false,
    slideBack: Boolean = false,
    clearStack: Boolean = false
) {
    (activity as? AppCompatActivity)?.navigateTo(
        container,
        destination,
        args,
        backStackTag,
        sharedElements,
        byFade,
        slideBack,
        clearStack
    )
}

fun Fragment.navigateToWithoutAnimation(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    clearStack: Boolean = false
) {
    (activity as? AppCompatActivity)?.navigateToWithoutAnimation(
        container,
        destination,
        args,
        backStackTag,
        sharedElements,
        clearStack
    )
}

fun Fragment.navigateChildTo(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    byFade: Boolean = false,
    slideBack: Boolean = false,
    clearStack: Boolean = false,
    animateNavigation: Boolean = true,
    manager : FragmentManager = childFragmentManager
) {
    if (clearStack) {
        manager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
    destination.arguments = args
    manager
        .beginTransaction()
        .apply {
            setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            sharedElements?.forEach {
                addSharedElement(it.first, it.second)
            }
            backStackTag?.let {
                addToBackStack(it)
            }
        }
        .replace(container, destination)
        .commit()
}

fun Fragment.navigateChildToWithoutAnimation(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    byFade: Boolean = false,
    slideBack: Boolean = false,
    clearStack: Boolean = false,
    animateNavigation: Boolean = true,
    manager : FragmentManager = childFragmentManager
) {
    if (clearStack) {
        manager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
    destination.arguments = args
    manager
        .beginTransaction()
        .apply {
            sharedElements?.forEach {
                addSharedElement(it.first, it.second)
            }
            backStackTag?.let {
                addToBackStack(it)
            }
        }
        .replace(container, destination)
        .commit()
}



fun AppCompatActivity.navigateTo(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    byFade: Boolean = false,
    slideBack: Boolean = false,
    clearStack: Boolean = false,
    animateNavigation: Boolean = true
) {
    if (clearStack) {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
    destination.arguments = args
    supportFragmentManager
        .beginTransaction()
        .apply {
            if(animateNavigation) {
                if (byFade) {
                    setCustomAnimations(
                        R.anim.fade_in, R.anim.fade_out,
                        R.anim.fade_in, R.anim.fade_out
                    )
                } else {
                    if (slideBack) {
                        setCustomAnimations(
                            com.company.imetlin.fishmarker.R.anim.enter_from_left,
                            com.company.imetlin.fishmarker.R.anim.exit_to_right,
                            com.company.imetlin.fishmarker.R.anim.enter_from_right,
                            com.company.imetlin.fishmarker.R.anim.exit_to_left
                        )
                    } else {
                        setCustomAnimations(
                            com.company.imetlin.fishmarker.R.anim.enter_from_right,
                            com.company.imetlin.fishmarker.R.anim.exit_to_left,
                            com.company.imetlin.fishmarker.R.anim.enter_from_left,
                            com.company.imetlin.fishmarker.R.anim.exit_to_right
                        )
                    }
                }
            }
            sharedElements?.forEach {
                addSharedElement(it.first, it.second)
            }
            backStackTag?.let {
                addToBackStack(it)
            }
        }
        .replace(container, destination)
        .commit()
}

fun AppCompatActivity.navigateToWithoutAnimation(
    @IdRes container: Int,
    destination: Fragment,
    args: Bundle? = null,
    backStackTag: String? = null,
    sharedElements: List<Pair<View, String>>? = null,
    clearStack: Boolean = false
) {
    if (clearStack) {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
    destination.arguments = args
    supportFragmentManager
        .beginTransaction()
        .apply {
            sharedElements?.forEach {
                addSharedElement(it.first, it.second)
            }
            backStackTag?.let {
                addToBackStack(it)
            }
        }
        .replace(container, destination)
        .commit()
}