/*
Copyright 2016 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.security.zynamics.reil.translators.x64;

import com.google.security.zynamics.reil.OperandSize;
import com.google.security.zynamics.reil.ReilHelpers;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.translators.IInstructionTranslator;
import com.google.security.zynamics.reil.translators.ITranslationEnvironment;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.TranslationHelpers;
import com.google.security.zynamics.reil.translators.TranslationResult;
import com.google.security.zynamics.zylib.disassembly.IInstruction;
import com.google.security.zynamics.zylib.disassembly.IOperandTree;

import java.util.List;


/**
 * Translates BTR instructions to REIL code.
 */
public class BtrTranslator implements IInstructionTranslator {

  /**
   * Translates a BTR instruction to REIL code.
   * 
   * @param environment A valid translation environment
   * @param instruction The BTR instruction to translate
   * @param instructions The generated REIL code will be added to this list
   * 
   * @throws InternalTranslationException if any of the arguments are null the passed instruction is
   *         not a BTR instruction
   */
  @Override
  public void translate(final ITranslationEnvironment environment, final IInstruction instruction,
      final List<ReilInstruction> instructions) throws InternalTranslationException {

    TranslationHelpers.checkTranslationArguments(environment, instruction, instructions, "btr");

    if (instruction.getOperands().size() != 2) {
      throw new InternalTranslationException(
          "Error: Argument instruction is not a btr instruction (invalid number of operands)");
    }

    final long baseOffset = instruction.getAddress().toLong() * 0x100;
    long offset = baseOffset;

    final IOperandTree targetOperand = instruction.getOperands().get(0);
    final IOperandTree sourceOperand = instruction.getOperands().get(1);

    // Load the target operand.
    final TranslationResult targetResult =
        Helpers.translateOperand(environment, offset, targetOperand, true);
    instructions.addAll(targetResult.getInstructions());

    offset = baseOffset + instructions.size();

    // Load the source operand.
    final TranslationResult sourceResult =
        Helpers.translateOperand(environment, offset, sourceOperand, true);
    instructions.addAll(sourceResult.getInstructions());

    offset = baseOffset + instructions.size();

    final String negatedIndex = environment.getNextVariableString();
    final String shiftedTarget = environment.getNextVariableString();

    instructions.add(ReilHelpers.createSub(offset++, OperandSize.BYTE, "0", sourceResult.getSize(),
        sourceResult.getRegister(), OperandSize.WORD, negatedIndex));
    instructions.add(ReilHelpers.createBsh(offset++, targetResult.getSize(),
        targetResult.getRegister(), OperandSize.BYTE, negatedIndex, targetResult.getSize(),
        shiftedTarget));

    instructions.add(ReilHelpers.createAnd(offset++, targetResult.getSize(), shiftedTarget,
        OperandSize.BYTE, "1", OperandSize.BYTE, Helpers.CARRY_FLAG));

    // Clear the bit in the destination
    final String shiftedIndex = environment.getNextVariableString();
    final String negatedShiftedIndex = environment.getNextVariableString();
    final String andedResult = environment.getNextVariableString();

    // Shift the mask to the right bit
    instructions.add(ReilHelpers.createBsh(offset++, OperandSize.BYTE, "1", sourceResult.getSize(),
        sourceResult.getRegister(), targetResult.getSize(), shiftedIndex));

    // Toggle the bits of the shift mask
    instructions.add(ReilHelpers.createXor(offset++, targetResult.getSize(), shiftedIndex,
        targetResult.getSize(),
        String.valueOf(TranslationHelpers.getAllBitsMask(targetResult.getSize())),
        targetResult.getSize(), negatedShiftedIndex));

    // Preserve all original bits except for the one at the shift position which is cleared
    instructions.add(ReilHelpers.createAnd(offset++, targetResult.getSize(),
        targetResult.getRegister(), targetResult.getSize(), negatedShiftedIndex,
        targetResult.getSize(), andedResult));

    Helpers.writeBack(environment, offset++, targetOperand, andedResult, targetResult.getSize(),
        targetResult.getAddress(), targetResult.getType(), instructions);
  }
}
