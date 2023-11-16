/*
 *   Catalyst - AnvilPowered.org
 *   Copyright (C) 2020-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.catalyst.core.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.core.command.CommandSource
import org.anvilpowered.catalyst.api.user.GameUser
import org.anvilpowered.catalyst.core.db.RepositoryScope
import org.anvilpowered.kbrig.argument.StringArgumentType
import org.anvilpowered.kbrig.builder.ArgumentBuilder
import org.anvilpowered.kbrig.builder.RequiredArgumentBuilder
import org.anvilpowered.kbrig.builder.executesSuspending
import org.anvilpowered.kbrig.context.CommandContext
import org.anvilpowered.kbrig.context.get

context(RepositoryScope)
fun ArgumentBuilder.Companion.gameUser(
    argumentName: String = "gameUser",
    command: suspend (context: CommandContext<CommandSource>, gameUser: GameUser) -> Int,
): RequiredArgumentBuilder<CommandSource, String> =
    required<CommandSource, String>(argumentName, StringArgumentType.SingleWord)
        .suggests { _, builder ->
            gameUserRepository.getAllUsernames(startWith = builder.input).forEach { name -> builder.suggest(name) }
            builder.build()
        }
        .executesSuspending { context ->
            val gameUserName = context.get<String>(argumentName)
            gameUserRepository.getByUsername(gameUserName)?.let { gameUser ->
                command(context, gameUser)
            } ?: run {
                context.source.audience.sendMessage(
                    Component.text()
                        .append(Component.text("GameUser with name ", NamedTextColor.RED))
                        .append(Component.text(gameUserName, NamedTextColor.GOLD))
                        .append(Component.text(" not found!", NamedTextColor.RED)),
                )
                0
            }
        }
