package breeze.linalg.operators

import breeze.generic.UFunc
import breeze.linalg._
import breeze.linalg.support.{CanCopy, CanZipMapKeyValues, CanZipMapValues}
import breeze.math.{Field, Ring, Semiring}
import breeze.storage.Zero
import scalaxy.debug.require

import scala.reflect.ClassTag

// TODO(names): fix names
trait HashVector_GenericOps {

  implicit def canSet_HV_Generic[V]: OpSet.InPlaceImpl2[HashVector[V], V] = {
    new OpSet.InPlaceImpl2[HashVector[V], V] {
      def apply(a: HashVector[V], b: V): Unit = {
        var i = 0
        while (i < a.length) {
          a(i) = b
          i += 1
        }

      }
    }
  }

  implicit def canSet_HV_HV_Generic[V]: OpSet.InPlaceImpl2[HashVector[V], Vector[V]] = {
    new OpSet.InPlaceImpl2[HashVector[V], Vector[V]] {
      def apply(a: HashVector[V], b: Vector[V]): Unit = {
        require(b.length == a.length, "Vectors must be the same length!")
        a.clear()
        for ((k, v) <- b.activeIterator) {
          a(k) = v
        }
      }
    }
  }

  implicit def HV_canGaxpy[V: Semiring]: scaleAdd.InPlaceImpl3[HashVector[V], V, HashVector[V]] = {
    new scaleAdd.InPlaceImpl3[HashVector[V], V, HashVector[V]] {
      val ring: Semiring[V] = implicitly[Semiring[V]]
      def apply(a: HashVector[V], s: V, b: HashVector[V]): Unit = {
        require(b.length == a.length, "Vectors must be the same length!")

        for ((k, v) <- b.activeIterator)
          a(k) = ring.+(a(k), ring.*(s, v))
      }
    }
  }

  class CanZipMapValuesHashVector[@specialized(Double, Int, Float, Long) V, @specialized(Int, Double) RV: ClassTag: Zero]
    extends CanZipMapValues[HashVector[V], V, RV, HashVector[RV]] {

    def create(length: Int) = HashVector.zeros(length)

    /**Maps all corresponding values from the two collections. */
    def map(from: HashVector[V], from2: HashVector[V], fn: (V, V) => RV) = {
      require(from.length == from2.length, "Vector lengths must match!")
      val result = create(from.length)
      var i = 0
      while (i < from.length) {
        result(i) = fn(from(i), from2(i))
        i += 1
      }
      result
    }

    def mapActive(from: HashVector[V], from2: HashVector[V], fn: (V, V) => RV) = {
      map(from, from2, fn)
    }

  }
  implicit def HV_zipMap[V, R: ClassTag: Zero]: CanZipMapValuesHashVector[V, R] = new CanZipMapValuesHashVector[V, R]
  implicit val HV_zipMap_d: CanZipMapValuesHashVector[Double, Double] = new CanZipMapValuesHashVector[Double, Double]
  implicit val HV_zipMap_f: CanZipMapValuesHashVector[Float, Float] = new CanZipMapValuesHashVector[Float, Float]
  implicit val HV_zipMap_i: CanZipMapValuesHashVector[Int, Int] = new CanZipMapValuesHashVector[Int, Int]

  class CanZipMapKeyValuesHashVector[@specialized(Double, Int, Float, Long) V, @specialized(Int, Double) RV: ClassTag: Zero]
    extends CanZipMapKeyValues[HashVector[V], Int, V, RV, HashVector[RV]] {

    def create(length: Int) = HashVector.zeros(length)

    /**Maps all corresponding values from the two collections. */
    def map(from: HashVector[V], from2: HashVector[V], fn: (Int, V, V) => RV) = {
      require(from.length == from2.length, "Vector lengths must match!")
      val result = create(from.length)
      var i = 0
      while (i < from.length) {
        result(i) = fn(i, from(i), from2(i))
        i += 1
      }
      result
    }

    override def mapActive(from: HashVector[V], from2: HashVector[V], fn: (Int, V, V) => RV): HashVector[RV] = {
      map(from, from2, fn)
    }
  }
  implicit def HV_zipMapKV[V, R: ClassTag: Zero]: CanZipMapKeyValuesHashVector[V, R] = new CanZipMapKeyValuesHashVector[V, R]

  implicit def HV_addIntoField[T](
                                   implicit field: Field[T],
                                   ct: ClassTag[T]): OpAdd.InPlaceImpl2[HashVector[T], HashVector[T]] = {
    new OpAdd.InPlaceImpl2[HashVector[T], HashVector[T]] {
      override def apply(v: HashVector[T], v2: HashVector[T]) = {
        for (i <- 0 until v.length) v(i) = field.+(v(i), v2(i))
      }
    }

  }

  implicit def HV_subIntoField[T](
                                   implicit field: Field[T],
                                   ct: ClassTag[T]): OpSub.InPlaceImpl2[HashVector[T], HashVector[T]] = {
    new OpSub.InPlaceImpl2[HashVector[T], HashVector[T]] {
      override def apply(v: HashVector[T], v2: HashVector[T]) = {
        for (i <- 0 until v.length) v(i) = field.-(v(i), v2(i))
      }
    }

  }

  implicit def HV_mulIntoField[T](
                                   implicit field: Field[T],
                                   ct: ClassTag[T]): OpMulScalar.InPlaceImpl2[HashVector[T], HashVector[T]] = {
    new OpMulScalar.InPlaceImpl2[HashVector[T], HashVector[T]] {
      override def apply(v: HashVector[T], v2: HashVector[T]) = {
        for (i <- 0 until v.length) v(i) = field.*(v(i), v2(i))
      }
    }

  }

  implicit def HV_divIntoField[T](
                                   implicit field: Field[T],
                                   ct: ClassTag[T]): OpDiv.InPlaceImpl2[HashVector[T], HashVector[T]] = {
    new OpDiv.InPlaceImpl2[HashVector[T], HashVector[T]] {
      override def apply(v: HashVector[T], v2: HashVector[T]) = {
        for (i <- 0 until v.length) v(i) = field./(v(i), v2(i))
      }
    }

  }

  implicit def HV_addIntoField[T](implicit field: Semiring[T], ct: ClassTag[T]): OpAdd.InPlaceImpl2[HashVector[T], T] = {
    new OpAdd.InPlaceImpl2[HashVector[T], T] {
      override def apply(v: HashVector[T], v2: T) = {
        for (i <- 0 until v.length) v(i) = field.+(v(i), v2)
      }
    }

  }



  implicit def HV_S_subIntoField[T](implicit field: Ring[T], ct: ClassTag[T]): OpSub.InPlaceImpl2[HashVector[T], T] = {
    new OpSub.InPlaceImpl2[HashVector[T], T] {
      override def apply(v: HashVector[T], v2: T) = {
        if (v2 == field.zero)
          return

        for (i <- 0 until v.length) v(i) = field.-(v(i), v2)
      }
    }

  }

  implicit def HV_S_mulScalarIntoField[T](
                                           implicit field: Semiring[T],
                                           ct: ClassTag[T]): OpMulScalar.InPlaceImpl2[HashVector[T], T] = {
    new OpMulScalar.InPlaceImpl2[HashVector[T], T] {
      override def apply(v: HashVector[T], v2: T) = {
        if (v2 == field.zero) {
          v := field.zero
        } else if (v2 == field.one) {
          return
        } else {
          for (i <- 0 until v.length) v(i) = field.*(v(i), v2)
        }
      }
    }
  }

  implicit def vDivIntoSField[T](implicit field: Field[T], ct: ClassTag[T]): OpDiv.InPlaceImpl2[HashVector[T], T] = {
    new OpDiv.InPlaceImpl2[HashVector[T], T] {
      override def apply(v: HashVector[T], v2: T) = {
        for (i <- 0 until v.length) v(i) = field./(v(i), v2)
      }
    }
  }

  implicit def vPowIntoS[T](
                             implicit pow: OpPow.Impl2[T, T, T],
                             ct: ClassTag[T]): OpPow.InPlaceImpl2[HashVector[T], T] = {
    new OpPow.InPlaceImpl2[HashVector[T], T] {
      override def apply(v: HashVector[T], v2: T) = {
        for (i <- 0 until v.length) v(i) = pow(v(i), v2)
      }
    }
  }

  // TODO: try removing
  implicit def HV_dotField[T](implicit field: Semiring[T]): OpMulInner.Impl2[HashVector[T], HashVector[T], T] = {
    new OpMulInner.Impl2[HashVector[T], HashVector[T], T] {
      override def apply(v: HashVector[T], v2: HashVector[T]): T = {
        var acc = field.zero
        for (i <- 0 until v.length) {
          acc = field.+(acc, field.*(v(i), v2(i)))
        }
        acc
      }
    }
  }

  implicit def impl_OpSet_InPlace_HV_HV_Generic[V]: OpSet.InPlaceImpl2[HashVector[V], HashVector[V]] = {

    new OpSet.InPlaceImpl2[HashVector[V], HashVector[V]] {
      def apply(a: HashVector[V], b: HashVector[V]): Unit = {
        require(b.length == a.length, "HashVectors must be the same length!")
        a.clear()
        b.array.copyTo(a.array)
      }
    }
  }


}