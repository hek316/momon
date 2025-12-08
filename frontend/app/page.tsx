'use client';

import Link from 'next/link';

export default function Home() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 px-4">
      <main className="w-full max-w-2xl text-center">
        <div className="mb-8">
          <h1 className="mb-4 text-6xl font-bold text-white">
            Momon
          </h1>
          <p className="text-2xl text-white/90">
            모몬 - AI가 만드는 당신만의 몬스터
          </p>
        </div>

        <div className="mb-12 rounded-2xl bg-white/10 p-8 backdrop-blur-lg">
          <p className="mb-4 text-lg text-white">
            사진과 감정을 공유하면 AI가 당신만의 특별한 몬스터를 만들어줍니다.
          </p>
          <p className="text-white/80">
            지금 당신의 기분은 어떤가요?
          </p>
        </div>

        <div className="space-y-4">
          <Link
            href="/create"
            className="block w-full rounded-full bg-white px-8 py-5 text-xl font-bold text-purple-600 shadow-2xl transition-all hover:scale-105 hover:shadow-3xl"
          >
            몬스터 소환하기 ✨
          </Link>

          <p className="text-sm text-white/70">
            아직 갤러리 기능은 준비 중입니다
          </p>
        </div>

        <div className="mt-16 text-sm text-white/60">
          <p>AI 기반 이미지 생성 서비스</p>
          <p className="mt-1">Powered by GPT-4o & DALL-E 3</p>
        </div>
      </main>
    </div>
  );
}
