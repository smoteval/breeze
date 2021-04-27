package breeze.linalg.operators

import breeze.generic.MappingUFuncOps
import breeze.linalg.SliceVectorOps

trait HasOps extends Any

object HasOps extends GenericOps with VectorOps
  with TensorLowPrio
  with DenseVectorOps
  with SparseVectorOps
  with HashVectorOps
  with MatrixOps
  with DenseMatrixOps
  with CSCMatrixOps
  with SliceVectorOps
  with BitVectorOps
  with MappingUFuncOps {}