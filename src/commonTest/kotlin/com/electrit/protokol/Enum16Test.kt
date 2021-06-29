package com.electrit.protokol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Enum16Test {

    class Enum16Data(var e: TestEnum16 = TestEnum16.INST_0)

    object BadEnum16DataProtokolObject : ProtokolObject<Enum16Data> {
        override val protokol: Protokol.(Enum16Data) -> Unit = {
            with(it) {
                ENUM8(::e, TestEnum16.values()) // ENUM8 is small for TestEnum16
            }
        }

        override fun create() = Enum16Data()
    }

    object Enum16DataProtokolObject : ProtokolObject<Enum16Data> {
        override val protokol: Protokol.(Enum16Data) -> Unit = {
            with(it) {
                ENUM16(::e, TestEnum16.values())
            }
        }

        override fun create() = Enum16Data()
    }

    object StrictEnum16DataProtokolObject : ProtokolObject<Enum16Data> {
        override val protokol: Protokol.(Enum16Data) -> Unit = {
            with(it) {
                ENUM16(::e, TestEnum16.values()) { value ->
                    if (value == TestEnum16.INST_0) throw IllegalArgumentException("value can't be INST_0")
                }
            }
        }

        override fun create() = Enum16Data()
    }

    @Test
    fun test() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.encode(Enum16Data(), BadEnum16DataProtokolObject)
        }

        fun assert(e: TestEnum16, po: ProtokolObject<Enum16Data>) {
            val bytes = ByteArrayProtokolCodec.encode(Enum16Data(e), po)
            val data = ByteArrayProtokolCodec.decode(bytes, po)
            assertEquals(e, data.e)
        }

        assert(TestEnum16.INST_0, Enum16DataProtokolObject)
        assert(TestEnum16.INST_256, Enum16DataProtokolObject)

        assertFailsWith<IllegalArgumentException> { assert(TestEnum16.INST_0, StrictEnum16DataProtokolObject) }
        assert(TestEnum16.INST_256, StrictEnum16DataProtokolObject)
    }

    @Test
    fun testParseError() {
        assertFailsWith<IllegalArgumentException> {
            ByteArrayProtokolCodec.decode(
                ByteArrayProtokolCodec.encode(Enum16Data(TestEnum16.INST_0), Enum16DataProtokolObject),
                StrictEnum16DataProtokolObject
            )
        }
    }

    enum class TestEnum16 {
        INST_0,
        INST_1,
        INST_2,
        INST_3,
        INST_4,
        INST_5,
        INST_6,
        INST_7,
        INST_8,
        INST_9,
        INST_10,
        INST_11,
        INST_12,
        INST_13,
        INST_14,
        INST_15,
        INST_16,
        INST_17,
        INST_18,
        INST_19,
        INST_20,
        INST_21,
        INST_22,
        INST_23,
        INST_24,
        INST_25,
        INST_26,
        INST_27,
        INST_28,
        INST_29,
        INST_30,
        INST_31,
        INST_32,
        INST_33,
        INST_34,
        INST_35,
        INST_36,
        INST_37,
        INST_38,
        INST_39,
        INST_40,
        INST_41,
        INST_42,
        INST_43,
        INST_44,
        INST_45,
        INST_46,
        INST_47,
        INST_48,
        INST_49,
        INST_50,
        INST_51,
        INST_52,
        INST_53,
        INST_54,
        INST_55,
        INST_56,
        INST_57,
        INST_58,
        INST_59,
        INST_60,
        INST_61,
        INST_62,
        INST_63,
        INST_64,
        INST_65,
        INST_66,
        INST_67,
        INST_68,
        INST_69,
        INST_70,
        INST_71,
        INST_72,
        INST_73,
        INST_74,
        INST_75,
        INST_76,
        INST_77,
        INST_78,
        INST_79,
        INST_80,
        INST_81,
        INST_82,
        INST_83,
        INST_84,
        INST_85,
        INST_86,
        INST_87,
        INST_88,
        INST_89,
        INST_90,
        INST_91,
        INST_92,
        INST_93,
        INST_94,
        INST_95,
        INST_96,
        INST_97,
        INST_98,
        INST_99,
        INST_100,
        INST_101,
        INST_102,
        INST_103,
        INST_104,
        INST_105,
        INST_106,
        INST_107,
        INST_108,
        INST_109,
        INST_110,
        INST_111,
        INST_112,
        INST_113,
        INST_114,
        INST_115,
        INST_116,
        INST_117,
        INST_118,
        INST_119,
        INST_120,
        INST_121,
        INST_122,
        INST_123,
        INST_124,
        INST_125,
        INST_126,
        INST_127,
        INST_128,
        INST_129,
        INST_130,
        INST_131,
        INST_132,
        INST_133,
        INST_134,
        INST_135,
        INST_136,
        INST_137,
        INST_138,
        INST_139,
        INST_140,
        INST_141,
        INST_142,
        INST_143,
        INST_144,
        INST_145,
        INST_146,
        INST_147,
        INST_148,
        INST_149,
        INST_150,
        INST_151,
        INST_152,
        INST_153,
        INST_154,
        INST_155,
        INST_156,
        INST_157,
        INST_158,
        INST_159,
        INST_160,
        INST_161,
        INST_162,
        INST_163,
        INST_164,
        INST_165,
        INST_166,
        INST_167,
        INST_168,
        INST_169,
        INST_170,
        INST_171,
        INST_172,
        INST_173,
        INST_174,
        INST_175,
        INST_176,
        INST_177,
        INST_178,
        INST_179,
        INST_180,
        INST_181,
        INST_182,
        INST_183,
        INST_184,
        INST_185,
        INST_186,
        INST_187,
        INST_188,
        INST_189,
        INST_190,
        INST_191,
        INST_192,
        INST_193,
        INST_194,
        INST_195,
        INST_196,
        INST_197,
        INST_198,
        INST_199,
        INST_200,
        INST_201,
        INST_202,
        INST_203,
        INST_204,
        INST_205,
        INST_206,
        INST_207,
        INST_208,
        INST_209,
        INST_210,
        INST_211,
        INST_212,
        INST_213,
        INST_214,
        INST_215,
        INST_216,
        INST_217,
        INST_218,
        INST_219,
        INST_220,
        INST_221,
        INST_222,
        INST_223,
        INST_224,
        INST_225,
        INST_226,
        INST_227,
        INST_228,
        INST_229,
        INST_230,
        INST_231,
        INST_232,
        INST_233,
        INST_234,
        INST_235,
        INST_236,
        INST_237,
        INST_238,
        INST_239,
        INST_240,
        INST_241,
        INST_242,
        INST_243,
        INST_244,
        INST_245,
        INST_246,
        INST_247,
        INST_248,
        INST_249,
        INST_250,
        INST_251,
        INST_252,
        INST_253,
        INST_254,
        INST_255,
        INST_256
    }

}
