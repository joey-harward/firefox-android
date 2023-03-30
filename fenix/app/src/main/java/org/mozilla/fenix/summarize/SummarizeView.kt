/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.summarize

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.mozilla.fenix.databinding.ComponentSummarizeBinding

class SummarizeView(container: ViewGroup) {
    private val binding = ComponentSummarizeBinding.inflate(LayoutInflater.from(container.context), container, true)

    fun update(summary: String) {
		if (summary.isNotEmpty()) {
			binding.summarizeProgress.visibility = View.GONE
		}
		binding.summarizeContent.text = summary
    }
}
