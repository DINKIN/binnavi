// Copyright 2011-2016 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.security.zynamics.binnavi.API.debug;

import com.google.common.base.Preconditions;
import com.google.security.zynamics.binnavi.API.disassembly.Address;
import com.google.security.zynamics.binnavi.debug.connection.packets.replies.ValidateMemoryReply;

public class DebuggerValidateMemoryReply extends DebuggerReply {

  final ValidateMemoryReply memoryReply;

  public DebuggerValidateMemoryReply(final ValidateMemoryReply reply) {
    super(reply);
    memoryReply = Preconditions.checkNotNull(reply, "Error: reply argument can not be null");
  }

  public Address getStartAddress() {
    return new Address(memoryReply.getStart().toBigInteger());
  }

  public Address getEndAddress() {
    return new Address(memoryReply.getEnd().toBigInteger());
  }
}
