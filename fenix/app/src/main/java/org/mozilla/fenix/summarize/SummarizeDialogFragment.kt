/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.summarize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.mapNotNull
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.lib.state.ext.flowScoped
import mozilla.components.support.ktx.android.content.getColorFromAttr
import mozilla.components.support.ktx.kotlinx.coroutines.flow.ifChanged
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentSummarizeDialogBinding
import org.mozilla.fenix.ext.requireComponents

class SummarizeDialogFragment : BottomSheetDialogFragment() {
	private val openAI = OpenAI("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")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_summarize_dialog, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSummarizeDialogBinding.bind(view)

		view.setBackgroundColor(view.context.getColorFromAttr(R.attr.layer1))

		val summarizeView = SummarizeView(
			container = binding.summarizeLayout,
		)

		requireComponents.core.store.flowScoped(viewLifecycleOwner) { flow ->
			flow.mapNotNull { state -> state.selectedTab?.content }
				.ifChanged()
				.collect { content ->
					val summary = summarizeUrl(content.url)
					summarizeView.update(summary.orEmpty())
				}
		}
    }

	@OptIn(BetaOpenAI::class)
	suspend fun summarizeUrl(url: String): String? {
		val chatCompletionRequest = ChatCompletionRequest(
			model = ModelId("gpt-3.5-turbo"),
			messages = listOf(
				ChatMessage(
					role = ChatRole.User,
					content = "Summarize $url"
				)
			)
		)
		val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
		return completion.choices[0].message?.content
	}

    companion object {
        val NAME: String = SummarizeDialogFragment::class.java.canonicalName?.substringAfterLast('.')
            ?: SummarizeDialogFragment::class.java.simpleName
    }
}
