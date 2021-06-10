/*
 *     Copyright (C) 2020 STG_Allen
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.catalyst.common.command

import com.google.inject.Inject
import com.mojang.brigadier.context.CommandContext
import org.anvilpowered.anvil.api.util.TextService
import org.anvilpowered.anvil.api.util.UserService
import org.anvilpowered.catalyst.api.plugin.PluginMessages
import org.anvilpowered.catalyst.api.service.PrivateMessageService

class ReplyCommand<TString, TPlayer : TCommandSource, TCommandSource> @Inject constructor(
  private val pluginMessages: PluginMessages<TString>,
  private val textService: TextService<TString, TCommandSource>,
  private val userService: UserService<TPlayer, TPlayer>,
  private val privateMessageService: PrivateMessageService<TString>
) {

  fun execute(context: CommandContext<TCommandSource>): Int {
    val message = context.getArgument("message", String::class.java)
    val senderUUID = userService.getUUID(context.source as TPlayer)
    if (privateMessageService.replyMap().containsKey(senderUUID)) {
      val recipientUUID = privateMessageService.replyMap()[senderUUID]
      val recipient = userService.getPlayer(recipientUUID)
      if (recipient.isPresent) {
        privateMessageService.sendMessage(
          userService.getUserName(context.source as TPlayer),
          userService.getUserName(recipient.get()),
          message
        )
        privateMessageService.replyMap()[userService.getUUID(recipient.get())] = userService.getUUID(context.source as TPlayer)
      } else {
        textService.send(pluginMessages.offlineOrInvalidPlayer(), context.source)
      }
    } else {
      textService.send(textService.builder().red().append("Nobody to reply to!").build(), context.source)
    }
    return 1
  }
}
