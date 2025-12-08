'use client';

import { useState, useEffect, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import api from '@/lib/api';
import Image from 'next/image';

interface Monster {
  id: number;
  imageUrl: string;
  name: string;
  description: string;
  createdAt?: string;
}

function ResultContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const monsterId = searchParams.get('id');

  const [monster, setMonster] = useState<Monster | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMonster = async () => {
      if (!monsterId) {
        setError('몬스터 ID가 없습니다.');
        setIsLoading(false);
        return;
      }

      try {
        const response = await api.get(`/api/v1/monsters/${monsterId}`);
        setMonster(response.data);
      } catch (err) {
        console.error('Failed to fetch monster:', err);
        setError('몬스터를 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchMonster();
  }, [monsterId]);

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-purple-400 via-pink-500 to-red-500">
        <div className="text-center">
          <div className="mb-4 h-16 w-16 animate-spin rounded-full border-4 border-white border-t-transparent"></div>
          <p className="text-xl font-semibold text-white">로딩 중...</p>
        </div>
      </div>
    );
  }

  if (error || !monster) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-purple-400 via-pink-500 to-red-500 px-4">
        <div className="w-full max-w-md rounded-2xl bg-white p-8 text-center shadow-2xl">
          <h2 className="mb-4 text-2xl font-bold text-red-600">오류 발생</h2>
          <p className="mb-6 text-gray-700">{error}</p>
          <button
            onClick={() => router.push('/')}
            className="w-full rounded-full bg-purple-500 px-6 py-3 text-white hover:bg-purple-600"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 px-4 py-8">
      <div className="w-full max-w-2xl rounded-2xl bg-white p-8 shadow-2xl">
        <h1 className="mb-6 text-center text-3xl font-bold text-gray-800">
          당신의 몬스터가 소환되었습니다! 🎉
        </h1>

        {/* Monster image */}
        <div className="relative mb-6 h-[400px] w-full overflow-hidden rounded-xl">
          <Image
            src={monster.imageUrl}
            alt={monster.name}
            fill
            className="object-contain"
            priority
          />
        </div>

        {/* Monster info */}
        <div className="mb-6 space-y-4">
          <div>
            <h2 className="mb-2 text-2xl font-bold text-purple-600">
              {monster.name}
            </h2>
            <p className="text-gray-700 leading-relaxed">{monster.description}</p>
          </div>
        </div>

        {/* Action buttons */}
        <div className="space-y-3">
          <button
            onClick={() => router.push('/create')}
            className="w-full rounded-full bg-gradient-to-r from-purple-500 to-pink-500 px-6 py-4 text-lg font-semibold text-white transition-all hover:from-purple-600 hover:to-pink-600"
          >
            새로운 몬스터 소환하기
          </button>
          <button
            onClick={() => router.push('/')}
            className="w-full rounded-full border-2 border-gray-300 px-6 py-3 text-gray-700 transition-colors hover:border-gray-400 hover:bg-gray-50"
          >
            내 몬스터 갤러리 보기
          </button>
        </div>
      </div>
    </div>
  );
}

export default function ResultPage() {
  return (
    <Suspense
      fallback={
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-purple-400 via-pink-500 to-red-500">
          <div className="text-center">
            <div className="mb-4 h-16 w-16 animate-spin rounded-full border-4 border-white border-t-transparent"></div>
            <p className="text-xl font-semibold text-white">로딩 중...</p>
          </div>
        </div>
      }
    >
      <ResultContent />
    </Suspense>
  );
}
