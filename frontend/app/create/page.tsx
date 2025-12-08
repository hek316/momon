'use client';

import { useState, useEffect, useRef, FormEvent, ChangeEvent } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import Image from 'next/image';

export default function CreateMonsterPage() {
  const router = useRouter();
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('');
  const [emotionText, setEmotionText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [loadingMessage, setLoadingMessage] = useState('몬스터 소환 중...');
  const [error, setError] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Handle image file selection
  const handleImageSelect = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (file.size > 10 * 1024 * 1024) {
        setError('이미지 크기는 10MB 이하여야 합니다.');
        return;
      }

      if (!file.type.startsWith('image/')) {
        setError('이미지 파일만 업로드할 수 있습니다.');
        return;
      }

      setSelectedImage(file);
      setError('');

      // Create preview URL
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  // Handle form submission
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!selectedImage) {
      setError('이미지를 선택해주세요.');
      return;
    }

    if (!emotionText.trim()) {
      setError('감정을 입력해주세요.');
      return;
    }

    if (emotionText.length > 100) {
      setError('감정은 100자 이하로 입력해주세요.');
      return;
    }

    setIsLoading(true);
    setError('');
    setLoadingMessage('몬스터 소환 중...');

    // Set up loading message timer (30 seconds)
    const messageTimer = setTimeout(() => {
      setLoadingMessage('거의 다 왔어요! 조금만 더 기다려주세요...');
    }, 30000);

    try {
      // Create FormData for multipart/form-data request
      const formData = new FormData();
      formData.append('image', selectedImage);
      formData.append('text', emotionText);

      // Send request to backend
      const response = await api.post('/api/v1/monsters', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        timeout: 90000, // 90 second timeout
      });

      clearTimeout(messageTimer);

      // Navigate to result page with monster data
      router.push(`/result?id=${response.data.id}`);
    } catch (err: unknown) {
      clearTimeout(messageTimer);
      console.error('Monster creation failed:', err);

      if (err instanceof Error) {
        setError(err.message || '몬스터 생성에 실패했습니다. 다시 시도해주세요.');
      } else {
        setError('몬스터 생성에 실패했습니다. 다시 시도해주세요.');
      }
      setIsLoading(false);
    }
  };

  // Prevent tab close during loading
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (isLoading) {
        e.preventDefault();
        e.returnValue = ''; // Required for Chrome
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [isLoading]);

  // Loading screen
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-purple-400 via-pink-500 to-red-500">
        <div className="text-center">
          <div className="mb-8">
            {/* Simple CSS animation */}
            <div className="relative mx-auto h-32 w-32">
              <div className="absolute inset-0 animate-ping rounded-full bg-white opacity-75"></div>
              <div className="relative flex h-full w-full items-center justify-center rounded-full bg-white">
                <span className="text-4xl">✨</span>
              </div>
            </div>
          </div>
          <h2 className="mb-4 text-3xl font-bold text-white">{loadingMessage}</h2>
          <p className="text-lg text-white/90">
            잠시만 기다려주세요. AI가 당신만의 몬스터를 만들고 있어요!
          </p>
        </div>
      </div>
    );
  }

  // Main form
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 px-4 py-8">
      <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-2xl">
        <h1 className="mb-6 text-center text-3xl font-bold text-gray-800">
          몬스터 소환하기
        </h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Image upload section */}
          <div>
            <label className="mb-2 block text-sm font-medium text-gray-700">
              사진 선택
            </label>
            <div
              onClick={() => fileInputRef.current?.click()}
              className="flex min-h-[200px] cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-gray-300 bg-gray-50 transition-colors hover:border-purple-500 hover:bg-purple-50"
            >
              {previewUrl ? (
                <div className="relative h-[200px] w-full">
                  <Image
                    src={previewUrl}
                    alt="Selected preview"
                    fill
                    className="rounded-lg object-contain"
                  />
                </div>
              ) : (
                <div className="text-center">
                  <svg
                    className="mx-auto h-12 w-12 text-gray-400"
                    stroke="currentColor"
                    fill="none"
                    viewBox="0 0 48 48"
                    aria-hidden="true"
                  >
                    <path
                      d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                      strokeWidth={2}
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                  <p className="mt-2 text-sm text-gray-600">
                    클릭하여 사진을 선택하세요
                  </p>
                  <p className="mt-1 text-xs text-gray-500">
                    PNG, JPG, GIF (최대 10MB)
                  </p>
                </div>
              )}
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleImageSelect}
              className="hidden"
            />
          </div>

          {/* Emotion text input */}
          <div>
            <label
              htmlFor="emotion"
              className="mb-2 block text-sm font-medium text-gray-700"
            >
              당신의 감정 ({emotionText.length}/100)
            </label>
            <textarea
              id="emotion"
              value={emotionText}
              onChange={(e) => setEmotionText(e.target.value)}
              maxLength={100}
              rows={4}
              placeholder="지금의 기분이나 감정을 표현해주세요..."
              className="w-full rounded-lg border border-gray-300 px-4 py-3 text-gray-900 focus:border-purple-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
            />
          </div>

          {/* Error message */}
          {error && (
            <div className="rounded-lg bg-red-50 p-4 text-sm text-red-600">
              {error}
            </div>
          )}

          {/* Submit button */}
          <button
            type="submit"
            disabled={!selectedImage || !emotionText.trim()}
            className="w-full rounded-full bg-gradient-to-r from-purple-500 to-pink-500 px-6 py-4 text-lg font-semibold text-white transition-all hover:from-purple-600 hover:to-pink-600 disabled:cursor-not-allowed disabled:opacity-50"
          >
            몬스터 소환 🔮
          </button>
        </form>

        {/* Back button */}
        <button
          onClick={() => router.push('/')}
          className="mt-4 w-full rounded-full border-2 border-gray-300 px-6 py-3 text-gray-700 transition-colors hover:border-gray-400 hover:bg-gray-50"
        >
          돌아가기
        </button>
      </div>
    </div>
  );
}
