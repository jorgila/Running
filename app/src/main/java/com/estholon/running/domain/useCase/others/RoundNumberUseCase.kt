package com.estholon.running.domain.useCase.others

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RoundNumberUseCase @Inject constructor(
    @ApplicationContext private val context: Context
){
    fun roundNumber(data: String, decimals: Int) : String{
        var d : String = data
        var p= d.indexOf(".", 0)
        if (p != null){
            var limit: Int = p+decimals +1
            if (d.length <= p+decimals+1) limit = d.length //-1
            d = d.subSequence(0, limit).toString()
        }
        return d
    }


}