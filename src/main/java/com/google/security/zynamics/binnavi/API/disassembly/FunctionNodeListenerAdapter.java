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

package com.google.security.zynamics.binnavi.API.disassembly;

import java.util.ArrayList;

import com.google.security.zynamics.binnavi.Gui.GraphWindows.CommentDialogs.Interfaces.IComment;


// / Adapter class for function nodes
/**
 * Adapter class that can be used by objects that want to listen on function nodes but only need to
 * process few events.
 */
public class FunctionNodeListenerAdapter implements IFunctionNodeListener {
  @Override
  public void appendedComment(final FunctionNode node, final IComment comment) {
    // Empty default implementation.
  }

  @Override
  public void deletedComment(final FunctionNode node, final IComment comment) {
    // Empty default implementation.
  }

  @Override
  public void editedComment(final FunctionNode node, final IComment comment) {
    // Empty default implementation.
  }

  @Override
  public void initializedComment(final FunctionNode node, final ArrayList<IComment> comments) {
    // Empty default implementation.
  }
}
