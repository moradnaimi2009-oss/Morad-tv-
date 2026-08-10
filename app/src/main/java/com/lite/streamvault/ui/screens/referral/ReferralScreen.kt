package com.lite.streamvault.ui.screens.referral

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lite.streamvault.ui.theme.Blue500
import com.lite.streamvault.viewmodel.ReferralViewModel

@Composable
fun ReferralScreen(
    onBack: () -> Unit,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "ادعُ أصدقاءك",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "كودك:",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = state.myCode,
                    color = Blue500,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "جرب تطبيق Morad TV! استخدم كود الدعوة: ${state.myCode}\nحمّل التطبيق: https://moradtv.lovestoblog.com/?i=${state.myCode}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "شارك كودك"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.height(18.dp))
                    Spacer(Modifier.height(0.dp))
                    Text("شارك كودك", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "دعوة ${state.referralCount} من ${state.goal}",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (state.referralCount.toFloat() / state.goal).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Blue500
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = when {
                state.unlocked -> "🎉 مبروك! تم فتح الحلقات الحصرية بقسم الكرتون"
                state.referralCount == 0 -> "ابدأ بدعوة أول صديق!"
                state.referralCount < 3 -> "🎉 بداية موفقة! أكمل كذا"
                state.referralCount < 6 -> "👏 استمر بنفس الطريقة"
                state.referralCount < state.goal -> "🔥 قريبة جداً! باقي ${state.goal - state.referralCount} بس"
                else -> "ادعُ ${state.goal - state.referralCount} صديق كمان لفتح الحلقات الحصرية بقسم الكرتون"
            },
            color = if (state.unlocked) Blue500 else Color.White.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = "عندك كود من صديق؟",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.redeemInput,
            onValueChange = viewModel::onRedeemInputChange,
            placeholder = { Text("اكتب الكود هنا") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.redeem() },
            enabled = !state.isRedeeming,
            colors = ButtonDefaults.buttonColors(containerColor = Blue500),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isRedeeming) "جاري التأكيد..." else "تأكيد الكود")
        }
        state.redeemMessage?.let { msg ->
            Text(
                text = msg,
                color = if (state.redeemSuccess == true) Blue500 else Color(0xFFEF5350),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (state.leaderboard.isNotEmpty()) {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "🏆 أفضل الداعين",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            state.leaderboard.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "#${index + 1}  ${entry.code}",
                        color = if (entry.code == state.myCode) Blue500 else Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${entry.referralCount}",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
